package com.poupa.attestationdeplacement;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.poupa.attestationdeplacement.generator.Attestation;
import com.poupa.attestationdeplacement.generator.AttestationDeplacementDerogatoireGenerator;
import com.poupa.attestationdeplacement.generator.AttestationGenerator;
import com.poupa.attestationdeplacement.ui.DateTextWatcher;

import java.util.Calendar;
import java.util.Date;

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

    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;

    private SharedPreferences.Editor edit;

    private AttestationGenerator attestationGenerator;

    private Attestation attestation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_attestation);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        initFields(true);
    }

    @Override
    public void onResume(){
        super.onResume();

        initFields(false);
    }

    /**
     * Initialize the input fields
     */
    private void initFields(boolean initDate) {
        attestation = new Attestation();

        attestationGenerator = new AttestationDeplacementDerogatoireGenerator(this, attestation);

        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        edit = userDetails.edit();

        surnameInput = findViewById(R.id.surname);

        surnameInput.setText(userDetails.getString("surname", ""));

        lastNameInput = findViewById(R.id.name);

        lastNameInput.setText(userDetails.getString("lastName", ""));

        birthDateInput = findViewById(R.id.birthdate);

        birthDateInput.setText(userDetails.getString("birthDate", ""));

        birthPlaceInput = findViewById(R.id.birthplace);

        birthPlaceInput.setText(userDetails.getString("birthPlace", ""));

        addressInput = findViewById(R.id.address);

        addressInput.setText(userDetails.getString("address", ""));

        cityInput = findViewById(R.id.city);

        cityInput.setText(userDetails.getString("city", ""));

        postalCodeInput = findViewById(R.id.postal_code);

        postalCodeInput.setText(userDetails.getString("postalCode", ""));

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
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
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

        setReasonsCheckboxes(userDetails);

        setDate();

        ImageView reasonsInfos = findViewById(R.id.reasonInfoImageView);
        reasonsInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReasonsDialog();
            }
        });

        constraintSet = new ConstraintSet();
        constraintLayout = findViewById(R.id.constraint_layout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                R.id.travel_hour_layout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);
    }

    private void setReasonsCheckboxes(SharedPreferences userDetails) {
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
     * @param v
     */
    public void startGenerate(View v) {
        boolean valid = checkFields();

        if (valid) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final GeneratePdfTask task = new GeneratePdfTask();
                    task.execute();
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

        if (! ((CheckBox) findViewById(R.id.reason1)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason2)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason3)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason4)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason5)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason6)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason7)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason8)).isChecked() &&
            ! ((CheckBox) findViewById(R.id.reason9)).isChecked()
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

    private class GeneratePdfTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            CreateAttestationActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                nDialog = new ProgressDialog(CreateAttestationActivity.this);
                nDialog.setMessage(getString(R.string.loading));
                nDialog.setTitle(getString(R.string.generating));
                nDialog.setIndeterminate(true);
                nDialog.setCancelable(false);
                nDialog.show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            CreateAttestationActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    nDialog.dismiss();

                    Toast.makeText(
                            CreateAttestationActivity.this,
                            CreateAttestationActivity.this.getString(R.string.attestation_generated),
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent show = new Intent(CreateAttestationActivity.this, MainActivity.class);
                    show.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(show);
                    finish();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            saveFields();

            attestationGenerator.generate();

            return null;
        }
    }

    /**
     * Save the user information locally for future use
     */
    public void saveFields() {
        attestation.setSurname(surnameInput.getText().toString());
        edit.putString("surname", attestation.getSurname());

        attestation.setLastName(lastNameInput.getText().toString());
        edit.putString("lastName", attestation.getLastName());

        attestation.setBirthDate(birthDateInput.getText().toString());
        edit.putString("birthDate", attestation.getBirthDate());

        attestation.setBirthPlace(birthPlaceInput.getText().toString());
        edit.putString("birthPlace", attestation.getBirthPlace());

        attestation.setAddress(addressInput.getText().toString());
        edit.putString("address", attestation.getAddress());

        attestation.setCity(cityInput.getText().toString());
        edit.putString("city", attestation.getCity());

        attestation.setPostalCode(postalCodeInput.getText().toString());
        edit.putString("postalCode", attestation.getPostalCode());

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
        attestation.setCurrentDate(currentDate);

        travelDateInput.setText(currentDate);

        String currentTime = String.format("%02d", currentHour) + 'h' + String.format("%02d", currentMinute);
        attestation.setCurrentTime(currentTime);

        travelHourInput.setText(currentTime);
    }
    
    private void getReasonsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_reasons, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(android.R.string.ok),null);
        builder.show();
    }
}
