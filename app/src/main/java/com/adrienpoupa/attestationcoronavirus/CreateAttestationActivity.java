package com.adrienpoupa.attestationcoronavirus;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CreateAttestationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_attestation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void setDate(String date) {
        TextInputEditText birthDate = findViewById(R.id.birthdate);
        birthDate.setText(date);
    }

    public void showFingerPaint(View v) {
        Intent intent = new Intent(this, FingerPaintActivity.class);
        startActivity(intent);
    }

    public void generate(View v) {
        TextInputEditText surnameInput = findViewById(R.id.surname);
        String surname = surnameInput.getText().toString();

        TextInputEditText nameInput = findViewById(R.id.name);
        String name = nameInput.getText().toString();

        TextInputEditText birthDateInput = findViewById(R.id.birthdate);
        String birthDate = birthDateInput.getText().toString();

        TextInputEditText addressInput = findViewById(R.id.address);
        String address = addressInput.getText().toString();

        TextInputEditText locationInput = findViewById(R.id.signatureLocation);
        String location = locationInput.getText().toString();

        AssetManager assetManager = getAssets();

        try {
            InputStream attestation = assetManager.open("attestation.pdf");

            Date d = new Date();
            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(d);

            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents/Attestations");

            // Create folders recursively
            docsFolder.mkdirs();

            File result = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + "-tmp.pdf");

            PdfDocument pdf =
                    new PdfDocument(new PdfReader(attestation), new PdfWriter(result));

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();


            fields.get("untitled1").setValue(surname + " " + name);
            fields.get("untitled2").setValue(birthDate);
            fields.get("untitled6").setValue(address);
            fields.get("untitled3").setValue(location);

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;

            fields.get("untitled4").setValue(String.format("%02d", day));
            fields.get("untitled5").setValue(String.format("%02d", month));

            if (((CheckBox) findViewById(R.id.reason1)).isChecked()) {
                fields.get("Case à cocher 1").setValue("Oui");
            }

            if (((CheckBox) findViewById(R.id.reason2)).isChecked()) {
                fields.get("Case à cocher 2").setValue("Oui");
            }

            if (((CheckBox) findViewById(R.id.reason3)).isChecked()) {
                fields.get("Case à cocher 3").setValue("Oui");
            }

            if (((CheckBox) findViewById(R.id.reason4)).isChecked()) {
                fields.get("Case à cocher 4").setValue("Oui");
            }

            if (((CheckBox) findViewById(R.id.reason5)).isChecked()) {
                fields.get("Case à cocher 5").setValue("Oui");
            }

            form.setNeedAppearances(true);
            form.setGenerateAppearance(true);

            form.flattenFields();

            pdf.close();

            File signatureFile = new File(Environment.getExternalStorageDirectory() + "/Documents/Attestations/signature.png");

            File generatedPdf = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + "-tmp.pdf");
            File finalPdf = new File(docsFolder.getAbsolutePath(),"Attestation-" + date + ".pdf");

            if (signatureFile.exists()) {
                byte[] bytesArray = new byte[(int) signatureFile.length()];

                FileInputStream fis = new FileInputStream(signatureFile);
                fis.read(bytesArray);
                fis.close();

                ImageData imageData = ImageDataFactory.create(bytesArray);

                PdfDocument pdfDocument = new PdfDocument(new PdfReader(generatedPdf), new PdfWriter(finalPdf));

                Document document = new Document(pdfDocument);
                Image pdfImg = new Image(imageData).scaleAbsolute(50, 100).setFixedPosition(1, 500, 15);

                document.add(pdfImg);
                document.close();

                generatedPdf.delete();
            } else {
                // Rename the tmp file to final
                generatedPdf.renameTo(finalPdf);
            }

            Toast.makeText(this, "Attestation générée !", Toast.LENGTH_SHORT).show();

            Intent show = new Intent(this, MainActivity.class);
            startActivity(show);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        }
    }
}
