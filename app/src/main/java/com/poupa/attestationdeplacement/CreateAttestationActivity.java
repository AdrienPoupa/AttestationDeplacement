package com.poupa.attestationdeplacement;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.poupa.attestationdeplacement.db.ProfileEntity;
import com.poupa.attestationdeplacement.db.ProfileViewModel;
import com.poupa.attestationdeplacement.generator.Attestation;
import com.poupa.attestationdeplacement.generator.AttestationGenerator;
import com.poupa.attestationdeplacement.tasks.GeneratePdfTask;
import com.poupa.attestationdeplacement.tasks.LoadProfilesCreateAttestationTask;
import com.poupa.attestationdeplacement.ui.DateTextWatcher;

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
        attestation = new Attestation();

        attestationGenerator = new AttestationGenerator(this, attestation);

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

        travelHourInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateAttestationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        travelHourInput.setText(String.format("%02d", hour) + "h" + String.format("%02d", minute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle(R.string.travel_hour);
                mTimePicker.show();
            }
        });

        setReasonsCheckboxes();

        setDate();

        ImageView reasonsInfos = findViewById(R.id.reasonInfoImageView);
        reasonsInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReasonsDialog();
            }
        });

        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                R.id.travel_hour_layout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.filled_exposed_dropdown);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProfileEntity profileEntity = (ProfileEntity) parent.getItemAtPosition(position);

                CreateAttestationActivity.this.fillFieldsFromProfile(profileEntity);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                (new LoadProfilesCreateAttestationTask(CreateAttestationActivity.this)).execute();
            }
        }).start();
    }

    private void setReasonsCheckboxes() {
        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        ((CheckBox) findViewById(R.id.reason1)).setChecked(userDetails.getBoolean("reason1", false));
        ((CheckBox) findViewById(R.id.reason2)).setChecked(userDetails.getBoolean("reason2", false));
        ((CheckBox) findViewById(R.id.reason3)).setChecked(userDetails.getBoolean("reason3", false));
        ((CheckBox) findViewById(R.id.reason4)).setChecked(userDetails.getBoolean("reason4", false));
        ((CheckBox) findViewById(R.id.reason5)).setChecked(userDetails.getBoolean("reason5", false));
        ((CheckBox) findViewById(R.id.reason6)).setChecked(userDetails.getBoolean("reason6", false));
        ((CheckBox) findViewById(R.id.reason7)).setChecked(userDetails.getBoolean("reason7", false));
        ((CheckBox) findViewById(R.id.reason8)).setChecked(userDetails.getBoolean("reason8", false));
        ((CheckBox) findViewById(R.id.reason9)).setChecked(userDetails.getBoolean("reason9", false));
    }

    /**
     * Generates the PDF by calling the async task
     *
     * @param v
     */
    public void startGenerate(View v) {
        boolean valid = checkFields();

        if (valid) {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                }
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

        if (!((CheckBox) findViewById(R.id.reason1)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason2)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason3)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason4)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason5)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason6)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason7)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason8)).isChecked() &&
                !((CheckBox) findViewById(R.id.reason9)).isChecked()
        ) {
            displayAlertDialog(getString(R.string.reaon_missing));
            return false;
        }

        return true;
    }

    private void displayAlertDialog(String text) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.warning)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
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

        attestation.setReason1(((CheckBox) findViewById(R.id.reason1)).isChecked());
        edit.putBoolean("reason1", attestation.isReason1());
        attestation.setReason2(((CheckBox) findViewById(R.id.reason2)).isChecked());
        edit.putBoolean("reason2", attestation.isReason2());
        attestation.setReason3(((CheckBox) findViewById(R.id.reason3)).isChecked());
        edit.putBoolean("reason3", attestation.isReason3());
        attestation.setReason4(((CheckBox) findViewById(R.id.reason4)).isChecked());
        edit.putBoolean("reason4", attestation.isReason4());
        attestation.setReason5(((CheckBox) findViewById(R.id.reason5)).isChecked());
        edit.putBoolean("reason5", attestation.isReason5());
        attestation.setReason6(((CheckBox) findViewById(R.id.reason6)).isChecked());
        edit.putBoolean("reason6", attestation.isReason6());
        attestation.setReason7(((CheckBox) findViewById(R.id.reason7)).isChecked());
        edit.putBoolean("reason7", attestation.isReason7());
        attestation.setReason8(((CheckBox) findViewById(R.id.reason8)).isChecked());
        edit.putBoolean("reason8", attestation.isReason8());
        attestation.setReason9(((CheckBox) findViewById(R.id.reason9)).isChecked());
        edit.putBoolean("reason9", attestation.isReason9());

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
