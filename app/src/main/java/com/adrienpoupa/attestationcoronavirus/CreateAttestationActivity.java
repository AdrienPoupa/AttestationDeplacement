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
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
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
    private PdfStamper stamper;
    private PdfReader reader;
    private String surname;
    private String lastName;
    private String location;
    private String address;
    private String birthPlace;
    private String birthDate;
    private String dateString;
    private int hour;
    private int minute;
    private String date;
    private Rectangle mediabox;
    private String fullTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_attestation);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        initFields();
    }

    /**
     * Initialize the input fields
     */
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

    /**
     * Generates the PDF file and the QRCode
     * @param v
     */
    public void generate(View v) {
        try {
            saveFields();

            setDate();

            AssetManager assetManager = getAssets();

            InputStream attestation = assetManager.open("attestation.pdf");

            reader = new PdfReader(attestation);

            stamper = new PdfStamper(reader, new FileOutputStream(getPdfPath()));

            mediabox = reader.getPageSize(1);

            fillForm();

            saveQrCode();

            addSmallQrCode();

            addText("Date de création:", 464, 150);
            addText(dateString + " à " + fullTime, 455, 144);

            addBigQrCode();

            stamper.setFormFlattening(true);

            stamper.close();

            Toast.makeText(this, "Attestation générée !", Toast.LENGTH_SHORT).show();

            Intent show = new Intent(this, MainActivity.class);
            startActivity(show);
        } catch (IOException | WriterException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the user information locally for future use
     */
    public void saveFields() {
        surname = surnameInput.getText().toString();

        edit.putString("surname", surname);

        lastName = lastNameInput.getText().toString();

        edit.putString("lastName", lastName);

        birthDate = birthDateInput.getText().toString();

        edit.putString("birthDate", birthDate);

        birthPlace = birthPlaceInput.getText().toString();

        edit.putString("birthPlace", birthPlace);

        address = addressInput.getText().toString();

        edit.putString("address", address);

        location = locationInput.getText().toString();

        edit.putString("location", location);

        edit.apply();
    }

    /**
     * Bootstrap the dates
     */
    public void setDate() {
        Date today = new Date();
        date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(today);

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        dateString = String.format("%02d", day) + '/' + String.format("%02d", month) + '/' + String.format("%02d", year);

        fullTime = String.format("%02d", hour) + "h" + String.format("%02d", minute);
    }

    /**
     * Fill the PDF form
     * @throws IOException
     * @throws DocumentException
     */
    public void fillForm() throws IOException, DocumentException {
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
    }

    /**
     * Save the QR code file
     * @throws WriterException
     * @throws IOException
     */
    private void saveQrCode() throws WriterException, IOException {
        Bitmap bitmapQrCode = this.generateQrCode(getQrCodeText(), 300, 300);

        File qrCodeFile = new File(getQrCodePath());

        FileOutputStream ostream = new FileOutputStream(qrCodeFile);

        bitmapQrCode.compress(Bitmap.CompressFormat.PNG, 10, ostream);

        ostream.close();
    }

    /**
     * Append the small QR code to page 1
     * @throws WriterException
     * @throws IOException
     * @throws DocumentException
     */
    private void addSmallQrCode() throws WriterException, IOException, DocumentException {
        Bitmap smallBitmapQrCode = this.generateQrCode(getQrCodeText(), 100, 100);

        byte[] byteArray = convertBitmapToByteArray(smallBitmapQrCode);

        Image image = Image.getInstance(byteArray);

        addImage(image, 1, mediabox.getWidth() - 170, 155);
    }

    /**
     * Append the big QR code to page 2
     * @throws IOException
     * @throws DocumentException
     */
    private void addBigQrCode() throws IOException, DocumentException {
        // Insert page 2
        stamper.insertPage(reader.getNumberOfPages() + 1,
                reader.getPageSizeWithRotation(1));

        Image image = Image.getInstance(getQrCodePath());

        addImage(image, 2, 50, mediabox.getHeight() - 350);
    }

    /**
     * Add text to the PDF
     * @param text
     * @param x
     * @param y
     */
    private void addText(String text, float x, float y) {
        Phrase phrase = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK));
        ColumnText.showTextAligned(stamper.getOverContent(1), Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    /**
     * Path of the QR code file
     * @return
     */
    public String getQrCodePath() {
        return getApplicationContext().getFilesDir() + "/Attestation-" + date + ".png";
    }

    /**
     * Path of the PDF file
     * @return
     */
    public String getPdfPath() {
        return getApplicationContext().getFilesDir() + "/Attestation-" + date + ".pdf";
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    public String getQrCodeText() {
        String dateHourString = dateString + " a " + fullTime;

        return "Cree le: " + dateHourString + "; Nom: " + lastName + "; Prenom: " + surname + "; " +
                "Naissance: " + birthDate + " a " + birthPlace + "; Adresse: " + address + "; " +
                "Sortie: " + dateHourString + "; Motifs: " + motives;
    }

    /**
     * Convert bitmap to array
     * @param bitmap
     * @return
     */
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream streamQrCode = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, streamQrCode);
        byte[] byteArray = streamQrCode.toByteArray();
        bitmap.recycle();

        return byteArray;
    }

    /**
     * Add an image to the PDF
     * @param image
     * @param page
     * @param x
     * @param y
     * @throws DocumentException
     * @throws IOException
     */
    public void addImage(Image image, int page, float x, float y) throws DocumentException, IOException {
        PdfImage stream = new PdfImage(image, "", null);

        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);

        image.setDirectReference(ref.getIndirectReference());
        image.setAbsolutePosition(x, y);

        PdfContentByte over = stamper.getOverContent(page);
        over.addImage(image);
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

    /**
     * Generates the QR Code from a string
     * @param str
     * @param width
     * @param height
     * @return
     * @throws WriterException
     */
    public Bitmap generateQrCode(String str, int width, int height) throws WriterException {
        BitMatrix result;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, height, hints);
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
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }
}
