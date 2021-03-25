package com.poupa.attestationdeplacement;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.poupa.attestationdeplacement.db.ProfileEntity;
import com.poupa.attestationdeplacement.db.ProfileViewModel;
import com.poupa.attestationdeplacement.dto.Attestation;
import com.poupa.attestationdeplacement.dto.CurfewAttestation;
import com.poupa.attestationdeplacement.dto.QuarantineAttestation;
import com.poupa.attestationdeplacement.dto.Reason;
import com.poupa.attestationdeplacement.generator.AttestationGenerator;
import com.poupa.attestationdeplacement.generator.CurfewAttestationGenerator;
import com.poupa.attestationdeplacement.generator.QuarantineAttestationGenerator;
import com.poupa.attestationdeplacement.tasks.GeneratePdfTask;
import com.poupa.attestationdeplacement.tasks.LoadProfilesCreateAttestationTask;
import com.poupa.attestationdeplacement.ui.DateTextWatcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateAttestationActivity extends AppCompatActivity {

    private TextInputEditText surnameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText birthDateInput;
    private TextInputEditText birthPlaceInput;
    private TextInputEditText addressInput;
    private TextInputEditText cityInput;
    private TextInputEditText postalCodeInput;
    private TextInputEditText travelDateInput;
    private TextInputEditText travelHourInput;

    private AttestationGenerator attestationGenerator;

    private Attestation attestation;

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_attestation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFields(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        initFields(false);
    }

    /**
     * Initialize the input fields
     */
    private void initFields(boolean initDate) {
        attestation = new QuarantineAttestation();
        attestationGenerator = new QuarantineAttestationGenerator(this, (QuarantineAttestation) attestation);

        LinearLayout curfewLayout = (LinearLayout) findViewById(R.id.reasons_curfew_layout);
        LinearLayout quarantineLayout = (LinearLayout) findViewById(R.id.reasons_quarantine_layout);

        AutoCompleteTextView autoCompleteAttestation = findViewById(R.id.attestation_type_dropdown);
        autoCompleteAttestation.setOnItemClickListener((parent, view, position, id) -> {
            attestation = (Attestation) parent.getItemAtPosition(position);

            if (attestation instanceof CurfewAttestation) {
                CurfewAttestation curfewAttestation = (CurfewAttestation) attestation;
                attestationGenerator = new CurfewAttestationGenerator(this, curfewAttestation);
                curfewLayout.setVisibility(View.VISIBLE);
                quarantineLayout.setVisibility(View.INVISIBLE);
            } else {
                QuarantineAttestation quarantineAttestation = (QuarantineAttestation) attestation;
                attestationGenerator = new QuarantineAttestationGenerator(this, quarantineAttestation);
                curfewLayout.setVisibility(View.INVISIBLE);
                quarantineLayout.setVisibility(View.VISIBLE);
            }
        });

        List<Attestation> attestations = new ArrayList<>();
        attestations.add(new QuarantineAttestation());
        attestations.add(new CurfewAttestation());

        ArrayAdapter<Attestation> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        attestations);

        autoCompleteAttestation.setAdapter(adapter);

        // https://stackoverflow.com/a/23568337/11115846
        if (Build.VERSION.SDK_INT > 16) {
            autoCompleteAttestation.setText(attestation.toString(), false);
        } else {
            ListAdapter adapter2 = autoCompleteAttestation.getAdapter();
            autoCompleteAttestation.setAdapter(null);
            autoCompleteAttestation.setText(attestation.toString());
            autoCompleteAttestation.setAdapter((ArrayAdapter) adapter2);
        }

        surnameInput = findViewById(R.id.surname);

        lastNameInput = findViewById(R.id.name);

        birthDateInput = findViewById(R.id.birthdate);

        birthPlaceInput = findViewById(R.id.birthplace);

        addressInput = findViewById(R.id.address);

        cityInput = findViewById(R.id.city);

        postalCodeInput = findViewById(R.id.postal_code);

        travelDateInput = findViewById(R.id.travel_date);

        travelHourInput = findViewById(R.id.travel_hour);

        if (initDate) {
            DateTextWatcher birthDateTextWatcher = new DateTextWatcher(birthDateInput);

            birthDateInput.addTextChangedListener(birthDateTextWatcher);

            DateTextWatcher travelDateTextWatcher = new DateTextWatcher(travelDateInput);

            travelDateInput.addTextChangedListener(travelDateTextWatcher);
        }

        travelHourInput.setOnClickListener(v -> {
            Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CreateAttestationActivity.this, (timePicker, hour1, minute1) -> travelHourInput.setText(String.format("%02d", hour1) + "h" + String.format("%02d", minute1)), hour, minute, true);
            mTimePicker.setTitle(R.string.travel_hour);
            mTimePicker.show();
        });

        setReasonsCheckboxes();

        setDate();

        ImageView reasonsInfo = findViewById(R.id.reasonInfoImageView);
        reasonsInfo.setOnClickListener(v -> getReasonsDialog());

        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                R.id.travel_hour_layout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        AutoCompleteTextView autoCompleteProfile = findViewById(R.id.filled_exposed_dropdown);
        autoCompleteProfile.setOnItemClickListener((parent, view, position, id) -> {
            ProfileEntity profileEntity = (ProfileEntity) parent.getItemAtPosition(position);

            CreateAttestationActivity.this.fillFieldsFromProfile(profileEntity);
        });

        new Thread(() -> (new LoadProfilesCreateAttestationTask(CreateAttestationActivity.this)).execute()).start();
    }

    private void setReasonsCheckboxes() {
        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        List<Reason> reasons = attestation.getReasons();
        for(int i = 0; i < reasons.size(); i++) {
            String reasonKey = "reason" + (i + 1) + "_" + attestation.getReasonPrefix();

            int resId = getResources().getIdentifier(reasonKey, "id", getPackageName());

            ((CheckBox) findViewById(resId)).setChecked(userDetails.getBoolean(reasonKey, false));
        }
    }

    /**
     * Generates the PDF by calling the async task
     *
     * @param v
     */
    public void startGenerate(View v) {
        boolean valid = checkFields();

        if (valid) {
            new Thread(() -> {
                (new GeneratePdfTask(CreateAttestationActivity.this, attestationGenerator)).execute();

                profileViewModel = new ProfileViewModel(getApplication());
                List<ProfileEntity> profileEntityList = profileViewModel.getAllProfiles();
                for (ProfileEntity profile : profileEntityList) {
                    if (profile.getFirstname().equals(surnameInput.getText().toString()) &&
                            profile.getLastname().equals(lastNameInput.getText().toString())) {
                        profile.setBirthdate(birthDateInput.getText().toString());
                        profile.setBirthplace(birthPlaceInput.getText().toString());
                        profile.setAddress(addressInput.getText().toString());
                        profile.setPostalcode(postalCodeInput.getText().toString());
                        profile.setCity(cityInput.getText().toString());
                        profileViewModel.update(profile);
                        return;
                    }
                }

                ProfileEntity currentProfile = new ProfileEntity(
                        surnameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        birthDateInput.getText().toString(),
                        birthPlaceInput.getText().toString(),
                        addressInput.getText().toString(),
                        postalCodeInput.getText().toString(),
                        cityInput.getText().toString());
                profileViewModel.insert(currentProfile);
            }).start();
        }
    }

    public boolean checkFields() {
        if (surnameInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.surname_missing));
            return false;
        }

        if (lastNameInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.lastname_missing));
            return false;
        }

        if (birthDateInput.getText().toString().equals("JJ/MM/AAAA")) {
            displayAlertDialog(getString(R.string.birthdate_missing));
            return false;
        }

        if (birthPlaceInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.birthplace_missing));
            return false;
        }

        if (addressInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.address_missing));
            return false;
        }

        if (cityInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.city_missing));
            return false;
        }

        if (postalCodeInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.postal_code_missing));
            return false;
        }

        if (travelDateInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.travel_date_missing));
            return false;
        }

        if (travelHourInput.getText().toString().isEmpty()) {
            displayAlertDialog(getString(R.string.travel_hour_missing));
            return false;
        }

        List<Reason> reasons = attestation.getReasons();
        for(int i = 0; i < reasons.size(); i++) {
            String reasonKey = "reason" + (i + 1) + "_" + attestation.getReasonPrefix();

            int resId = getResources().getIdentifier(reasonKey, "id", getPackageName());

            boolean isReasonEnabled = ((CheckBox) findViewById(resId)).isChecked();

            // At least one reason is enabled, move on
            if (isReasonEnabled) {
                return true;
            }
        }

        // No reason were enabled
        displayAlertDialog(getString(R.string.reaon_missing));
        return false;
    }

    private void displayAlertDialog(String text) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.warning)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void fillFieldsFromProfile(ProfileEntity profileEntity) {
        surnameInput = findViewById(R.id.surname);
        surnameInput.setText(profileEntity.getFirstname());

        lastNameInput = findViewById(R.id.name);
        lastNameInput.setText(profileEntity.getLastname());

        birthDateInput = findViewById(R.id.birthdate);
        birthDateInput.setText(profileEntity.getBirthdate());

        birthPlaceInput = findViewById(R.id.birthplace);
        birthPlaceInput.setText(profileEntity.getBirthplace());

        addressInput = findViewById(R.id.address);
        addressInput.setText(profileEntity.getAddress());

        cityInput = findViewById(R.id.city);
        cityInput.setText(profileEntity.getCity());

        postalCodeInput = findViewById(R.id.postal_code);
        postalCodeInput.setText(profileEntity.getPostalcode());
    }

    /**
     * Save the user information locally for future use
     */
    public void saveFields() {
        attestation.setSurname(surnameInput.getText().toString());

        attestation.setLastName(lastNameInput.getText().toString());

        attestation.setBirthDate(birthDateInput.getText().toString());

        attestation.setBirthPlace(birthPlaceInput.getText().toString());

        attestation.setAddress(addressInput.getText().toString());

        attestation.setCity(cityInput.getText().toString());

        attestation.setPostalCode(postalCodeInput.getText().toString());

        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        SharedPreferences.Editor edit = userDetails.edit();

        List<Reason> reasons = attestation.getReasons();
        for(int i = 0; i < reasons.size(); i++) {
            String reasonKey = "reason" + (i + 1) + "_" + attestation.getReasonPrefix();

            int resId = getResources().getIdentifier(reasonKey, "id", getPackageName());

            boolean isReasonEnabled = ((CheckBox) findViewById(resId)).isChecked();

            reasons.get(i).setEnabled(isReasonEnabled);

            edit.putBoolean(reasonKey, isReasonEnabled);
        }

        edit.apply();

        // Do not save this for further uses
        String travelDate = travelDateInput.getText().toString();
        attestation.setTravelDate(travelDate);
        String travelHour = travelHourInput.getText().toString();
        attestation.setTravelHour(travelHour);

        String[] hourMinute = travelHour.split("h");

        attestation.setHour(hourMinute[0]);
        attestation.setMinute(hourMinute[1]);
    }

    /**
     * Bootstrap the dates
     */
    public void setDate() {
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);

        String currentDay = String.format("%02d", day);
        String currentMonth = String.format("%02d", month);

        String currentDate = currentDay + '/' + currentMonth + '/' + String.format("%02d", year);

        travelDateInput.setText(currentDate);

        String currentTime = String.format("%02d", currentHour) + 'h' + String.format("%02d", currentMinute);

        travelHourInput.setText(currentTime);
    }

    private void getReasonsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_reasons, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(android.R.string.ok), null);
        builder.show();
    }
}
