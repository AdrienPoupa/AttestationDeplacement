package com.poupa.attestationdeplacement;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;

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
import com.poupa.attestationdeplacement.dto.Reason;
import com.poupa.attestationdeplacement.generator.AttestationGenerator;
import com.poupa.attestationdeplacement.tasks.GeneratePdfTask;
import com.poupa.attestationdeplacement.tasks.LoadProfilesCreateAttestationTask;
import com.poupa.attestationdeplacement.ui.DateTextWatcher;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CreateAttestationActivity extends AppCompatActivity {

    private static final String URI_SCHEME="attestation";
    private static final String URI_GENERATE = "/generate";
    private static final String URI_PARAM_SURNAME = "surname";
    private static final String URI_PARAM_LAST_NAME = "lastname";
    private static final String URI_PARAM_CITY = "city";
    private static final String URI_PARAM_POSTAL_CODE = "postalcode";
    private static final String URI_PARAM_ADDRESS = "address";
    private static final String URI_PARAM_BIRTH_PLACE = "birthplace";
    private static final String URI_PARAM_BIRTH_DATE = "birthdate";

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

        Intent intent = getIntent() ;
        if( intent != null ) {

            Uri uri = intent.getData();

            if (uri != null ) {
                fillFieldsFromUri(uri);

                startGenerate(findViewById(R.id.signatureButton));
            }
        }
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

        ImageView reasonsInfos = findViewById(R.id.reasonInfoImageView);
        reasonsInfos.setOnClickListener(v -> getReasonsDialog());

        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                R.id.travel_hour_layout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.filled_exposed_dropdown);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            ProfileEntity profileEntity = (ProfileEntity) parent.getItemAtPosition(position);

            CreateAttestationActivity.this.fillFieldsFromProfile(profileEntity);
        });

        new Thread(() -> (new LoadProfilesCreateAttestationTask(CreateAttestationActivity.this)).execute()).start();
    }

    private void setReasonsCheckboxes() {
        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        for(int i = 1; i < 10; i++) {
            int resId = getResources().getIdentifier("reason" + i, "id", getPackageName());

            ((CheckBox) findViewById(resId)).setChecked(userDetails.getBoolean("reason" + i, false));
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

        for(int i = 1; i < 10; i++) {
            int resId = getResources().getIdentifier("reason" + i, "id", getPackageName());

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

        List<Reason> reasons = attestation.getReasons();
        for(int i = 0; i < reasons.size(); i++) {
            String reasonKey = "reason" + (i + 1);

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

    /**
     * Create or update a dynamic shortcut
     */
    public void createShortcut() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme(URI_SCHEME)
                    .path(URI_GENERATE)
                    .appendQueryParameter(URI_PARAM_SURNAME, attestation.getSurname())
                    .appendQueryParameter(URI_PARAM_LAST_NAME, attestation.getLastName())
                    .appendQueryParameter(URI_PARAM_BIRTH_DATE, attestation.getBirthDate())
                    .appendQueryParameter(URI_PARAM_BIRTH_PLACE, attestation.getBirthPlace())
                    .appendQueryParameter(URI_PARAM_ADDRESS, attestation.getAddress())
                    .appendQueryParameter(URI_PARAM_CITY, attestation.getCity())
                    .appendQueryParameter(URI_PARAM_POSTAL_CODE, attestation.getPostalCode());

            List<Reason> reasons = attestation.getReasons();
            for(int i = 0; i < reasons.size(); i++) {
                String reasonKey = "reason" + (i + 1);

                if( reasons.get(i).isEnabled() ) {
                    uriBuilder.appendQueryParameter(reasonKey, "true");
                }
            }

            Uri shortcutUri = uriBuilder.build();

            final String id = shortcutUri.toString();

            // looking for a shortcut with same id
            ShortcutInfo matchingShortcut = shortcutManager.getDynamicShortcuts().stream().filter(shortcutInfo -> id.equals(shortcutInfo.getId())).findFirst().orElse(null);

            if (matchingShortcut != null) {
                shortcutManager.reportShortcutUsed(id);
            } else {
                String shortLabel = attestation.getReasonsDatabase();

                Intent intent = new Intent(Intent.ACTION_VIEW, shortcutUri)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                ShortcutInfo shortcut = new ShortcutInfo.Builder(this, id)
                        .setShortLabel(shortLabel)
                        .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                        .setIntent(intent)
                        .build();

                shortcutManager.addDynamicShortcuts(Collections.singletonList(shortcut));

                // manage dynamic shortcuts list size
                int maxDynamicShortcuts = Math.max(0, 4 - shortcutManager.getManifestShortcuts().size());
                List<String> shortcutsToDelete = shortcutManager.getDynamicShortcuts()
                        .stream()
                        .sorted(new Comparator<ShortcutInfo>() {
                            @Override
                            public int compare(ShortcutInfo lhs, ShortcutInfo rhs) {
                                return Long.compare(rhs.getLastChangedTimestamp(), lhs.getLastChangedTimestamp());
                            }
                        })
                        .skip(maxDynamicShortcuts)
                        .map(new Function<ShortcutInfo, String>() {
                            @Override
                            public String apply(ShortcutInfo s) {
                                return s.getId();
                            }
                        })
                        .collect(Collectors.<String>toList());

                shortcutManager.removeDynamicShortcuts(shortcutsToDelete);
            }
        }
    }

    public void fillFieldsFromUri(Uri shortcut) {

        if(shortcut == null) {
            return;
        }

        if (URI_GENERATE.equals(shortcut.getPath())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                String shortcutId = shortcut.toString();
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                shortcutManager.reportShortcutUsed(shortcutId);
            }

            surnameInput.setText(shortcut.getQueryParameter(URI_PARAM_SURNAME));
            lastNameInput.setText(shortcut.getQueryParameter(URI_PARAM_LAST_NAME));
            birthDateInput.setText(shortcut.getQueryParameter(URI_PARAM_BIRTH_DATE));
            birthPlaceInput.setText(shortcut.getQueryParameter(URI_PARAM_BIRTH_PLACE));
            addressInput.setText(shortcut.getQueryParameter(URI_PARAM_ADDRESS));
            cityInput.setText(shortcut.getQueryParameter(URI_PARAM_CITY));
            postalCodeInput.setText(shortcut.getQueryParameter(URI_PARAM_POSTAL_CODE));

            List<Reason> reasons = attestation.getReasons();
            for(int i = 0; i < reasons.size(); i++) {
                String reasonKey = "reason" + (i + 1);

                int resId = getResources().getIdentifier(reasonKey, "id", getPackageName());

                ((CheckBox) findViewById(resId)).setChecked(Boolean.parseBoolean(shortcut.getQueryParameter(reasonKey)));
            }

            setDate();
        }
    }
}
