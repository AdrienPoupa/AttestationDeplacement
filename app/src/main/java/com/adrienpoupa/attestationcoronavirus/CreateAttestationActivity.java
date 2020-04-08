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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        birthDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
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

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            String dateString = String.format("%02d", day) + '/' + String.format("%02d", month) + '/' + String.format("%02d", year);

            PdfReader reader = new PdfReader(attestation);

            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(docsFolder.getAbsolutePath()+"/Attestation-" + date + ".pdf"));

            AcroFields form = stamper.getAcroFields();

            String fullName = surname + " " + lastName;

            form.setField("Nom et prénom", fullName);
            form.setField("Signature", fullName);
            form.setField("Date de naissance", birthDate);
            form.setField("Lieu de naissance", birthPlace);
            form.setField("Adresse actuelle", address);
            form.setField("Ville", location);

            form.setField("Date", dateString);

            form.setField("Heure", String.format("%02d", hour));
            form.setField("Minute", String.format("%02d", minute));

            if (((CheckBox) findViewById(R.id.reason1)).isChecked()) {
                form.setField("Déplacements entre domicile et travail", "Oui");
                addMotive("travail");
            }

            if (((CheckBox) findViewById(R.id.reason2)).isChecked()) {
                form.setField("Déplacements achats nécéssaires", "Oui");
                addMotive("courses");
            }

            if (((CheckBox) findViewById(R.id.reason3)).isChecked()) {
                form.setField("Consultations et soins", "Oui");
                addMotive("sante");
            }

            if (((CheckBox) findViewById(R.id.reason4)).isChecked()) {
                form.setField("Déplacements pour motif familial", "Oui");
                addMotive("famille");
            }

            if (((CheckBox) findViewById(R.id.reason5)).isChecked()) {
                form.setField("Déplacements brefs (activité physique et animaux)", "Oui");
                addMotive("sport");
            }

            if (((CheckBox) findViewById(R.id.reason6)).isChecked()) {
                form.setField("Convcation judiciaire ou administrative", "Oui");
                addMotive("judiciaire");
            }

            if (((CheckBox) findViewById(R.id.reason7)).isChecked()) {
                form.setField("Mission d'intérêt général", "Oui");
                addMotive("missions");
            }

            stamper.setFormFlattening(true);

            stamper.close();

            String dateHourString = dateString + " a " + String.format("%02d", hour) + "h" + String.format("%02d", minute);

            String qrCodeString = "Cree le: " + dateHourString + "; Nom: " + lastName + "; Prenom: " + surname + "; Naissance: " + birthDate + " a " + birthPlace + "; Adresse: " + address + "; Sortie:" + dateHourString + "; Motifs: " + motives;

            Bitmap bitmap = this.generateQrCode(qrCodeString);

            File qrCodeFile = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + ".png");

            FileOutputStream ostream = new FileOutputStream(qrCodeFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);

            ostream.close();

            Toast.makeText(this, "Attestation générée !", Toast.LENGTH_SHORT).show();

            Intent show = new Intent(this, MainActivity.class);
            startActivity(show);
        } catch (IOException | WriterException | DocumentException e) {
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
            motives.append("-");
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
