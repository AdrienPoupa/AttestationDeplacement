package com.adrienpoupa.attestationcoronavirus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class CreateAttestationActivity extends AppCompatActivity {

    private TextInputEditText surnameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText birthDateInput;
    private TextInputEditText birthPlaceInput;
    private TextInputEditText addressInput;
    private TextInputEditText locationInput;

    private SharedPreferences.Editor edit;

    private StringBuilder motives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_attestation);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        initFields();
    }

    private void initFields() {
        motives = new StringBuilder();

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

        locationInput = findViewById(R.id.signatureLocation);

        locationInput.setText(userDetails.getString("location", ""));
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void setDate(String date) {
        TextInputEditText birthDate = findViewById(R.id.birthdate);
        birthDate.setText(date);
    }

    public void generate(View v) {
        String surname = surnameInput.getText().toString();

        edit.putString("surname", surname);

        String lastName = lastNameInput.getText().toString();

        edit.putString("lastName", lastName);

        String birthDate = birthDateInput.getText().toString();

        edit.putString("birthDate", birthDate);

        String birthPlace = birthPlaceInput.getText().toString();

        edit.putString("birthPlace", birthPlace);

        String address = addressInput.getText().toString();

        edit.putString("address", address);

        String location = locationInput.getText().toString();

        edit.putString("location", location);

        edit.apply();

        AssetManager assetManager = getAssets();

        try {
            InputStream attestation = assetManager.open("attestation.pdf");

            Date d = new Date();
            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(d);

            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents/Attestations");

            // Create folders recursively
            docsFolder.mkdirs();

            File result = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + ".pdf");

            PdfDocument pdf =
                    new PdfDocument(new PdfReader(attestation), new PdfWriter(result));

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            String fullName = surname + " " + lastName;

            fields.get("Nom et prénom").setValue(fullName);
            fields.get("Signature").setValue(fullName);
            fields.get("Date de naissance").setValue(birthDate);
            fields.get("Lieu de naissance").setValue(birthPlace);
            fields.get("Adresse actuelle").setValue(address);
            fields.get("Ville").setValue(location);

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);

            String dateString = String.format("%02d", day) + '/' + String.format("%02d", month) + '/' + String.format("%02d", year);

            fields.get("Date").setValue(dateString);

            fields.get("Heure").setValue(String.format("%02d", hour));
            fields.get("Minute").setValue(String.format("%02d", minute));

            if (((CheckBox) findViewById(R.id.reason1)).isChecked()) {
                fields.get("Déplacements entre domicile et travail").setValue("Oui");
                addMotive("travail");
            }

            if (((CheckBox) findViewById(R.id.reason2)).isChecked()) {
                fields.get("Déplacements achats nécéssaires").setValue("Oui");
                addMotive("courses");
            }

            if (((CheckBox) findViewById(R.id.reason3)).isChecked()) {
                fields.get("Consultations et soins").setValue("Oui");
                addMotive("sante");
            }

            if (((CheckBox) findViewById(R.id.reason4)).isChecked()) {
                fields.get("Déplacements pour motif familial").setValue("Oui");
                addMotive("familial");
            }

            if (((CheckBox) findViewById(R.id.reason5)).isChecked()) {
                fields.get("Déplacements brefs (activité physique et animaux)").setValue("Oui");
                addMotive("bref");
            }

            if (((CheckBox) findViewById(R.id.reason6)).isChecked()) {
                fields.get("Convcation judiciaire ou administrative").setValue("Oui");
                addMotive("convocation");
            }

            if (((CheckBox) findViewById(R.id.reason7)).isChecked()) {
                fields.get("Mission d'intérêt général").setValue("Oui");
                addMotive("intérêt général");
            }

            form.setNeedAppearances(true);
            form.setGenerateAppearance(true);

            form.flattenFields();

            pdf.close();

            String dateHourString = dateString + " a " + hour + "h" + minute;

            String qrCodeString = "Cree le: " + dateHourString + "; Nom: " + lastName + "; Prenom: " + surname + "; Naissance: " + birthDate + " a " + birthPlace + "; Adresse: " + address + "; Sortie:" + dateHourString + "; Motifs: " + motives;

            Bitmap bitmap = this.generateQrCode(qrCodeString);

            File qrCodeFile = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + ".png");

            FileOutputStream ostream = new FileOutputStream(qrCodeFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);

            ostream.close();

            Toast.makeText(this, "Attestation générée !", Toast.LENGTH_SHORT).show();

            Intent show = new Intent(this, MainActivity.class);
            startActivity(show);
        } catch (IOException | WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add motives
     * @param motive motive to add
     */
    public void addMotive(String motive) {
        if (motives.length() != 0) {
            motives.append("-").append(motive);
        }
        motives.append(motive);
    }


    public final static int WIDTH = 500;
    public final static int HEIGHT = 500;

    public Bitmap generateQrCode(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }
}
