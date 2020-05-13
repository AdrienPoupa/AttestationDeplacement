package com.poupa.attestationdeplacement;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.poupa.attestationdeplacement.db.AttestationDao;
import com.poupa.attestationdeplacement.db.AttestationDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private TextInputEditText cityInput;
    private TextInputEditText destinationCityInput;
    private TextInputEditText destinationDepartmentInput;
    private TextInputEditText postalCodeInput;
    private TextInputEditText travelDateInput;
    private TextInputEditText travelHourInput;
    private TextInputLayout travelHourLayout;

    private TextInputLayout destinationCityLayout;
    private TextInputLayout destinationDepartmentLayout;
    private LinearLayout recurringLayout;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;

    private SharedPreferences.Editor edit;

    private StringBuilder motivesQrCode;
    private StringBuilder motivesDatabase;
    private PdfStamper stamper;
    private PdfReader reader;
    private String surname;
    private String lastName;
    private String city;
    private String destinationCity;
    private String destinationDepartment;
    private String postalCode;
    private String address;
    private String birthPlace;
    private String birthDate;
    private String travelDate;
    private String travelHour;
    private String hour;
    private String minute;


    private Rectangle mediabox;
    private String currentTime;
    private String currentDate;
    private AttestationDao dao;

    private AttestationType attestationType;
    private AcroFields form;
    private String currentDay;
    private String currentMonth;

    enum AttestationType {
        DECLARATION_DEPLACEMENT,
        AUTO_ATTESTATION_TRANSPORTS,
        ATTESTATION_DEPLACEMENT_DEROGATOIRE
    }

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
        motivesQrCode = new StringBuilder();
        motivesDatabase = new StringBuilder();

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

        destinationCityInput = findViewById(R.id.destination_city);

        destinationCityInput.setText(userDetails.getString("destinationCity", ""));

        destinationDepartmentInput = findViewById(R.id.destination_department);

        destinationDepartmentInput.setText(userDetails.getString("destinationDepartment", ""));

        travelDateInput = findViewById(R.id.travel_date);

        travelHourInput = findViewById(R.id.travel_hour);

        DateTextWatcher birthDateTextWatcher = new DateTextWatcher(birthDateInput);

        birthDateInput.addTextChangedListener(birthDateTextWatcher);

        DateTextWatcher travelDateTextWatcher = new DateTextWatcher(travelDateInput);

        travelDateInput.addTextChangedListener(travelDateTextWatcher);

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

        setDate();

        ImageView reasonsInfos = findViewById(R.id.reasonInfoImageView);
        reasonsInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReasonsDialog();
            }
        });

        travelHourLayout = findViewById(R.id.travel_hour_layout);
        destinationCityLayout = findViewById(R.id.destination_city_layout);
        destinationDepartmentLayout = findViewById(R.id.destination_department_layout);
        recurringLayout = findViewById(R.id.recurring_layout);
        constraintLayout = findViewById(R.id.constraint_layout);

        constraintSet = new ConstraintSet();

        attestationType = AttestationType.AUTO_ATTESTATION_TRANSPORTS;

        Spinner spinner = findViewById(R.id.attestation_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attestation_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    attestationType = AttestationType.DECLARATION_DEPLACEMENT;
                    travelHourLayout.setVisibility(View.INVISIBLE);
                    destinationCityLayout.setVisibility(View.VISIBLE);
                    destinationDepartmentLayout.setVisibility(View.VISIBLE);
                    recurringLayout.setVisibility(View.VISIBLE);

                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                            R.id.recurring_layout, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(constraintLayout);

                } else if (position == 1) {
                    attestationType = AttestationType.AUTO_ATTESTATION_TRANSPORTS;
                    travelHourLayout.setVisibility(View.INVISIBLE);
                    destinationCityLayout.setVisibility(View.INVISIBLE);
                    destinationDepartmentLayout.setVisibility(View.INVISIBLE);
                    recurringLayout.setVisibility(View.INVISIBLE);

                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                            R.id.travel_date_layout, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(constraintLayout);

                } else {
                    attestationType = AttestationType.ATTESTATION_DEPLACEMENT_DEROGATOIRE;
                    travelHourLayout.setVisibility(View.VISIBLE);
                    destinationCityLayout.setVisibility(View.INVISIBLE);
                    destinationDepartmentLayout.setVisibility(View.INVISIBLE);
                    recurringLayout.setVisibility(View.INVISIBLE);

                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.reasonsTextView, ConstraintSet.TOP,
                            R.id.travel_hour_layout, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(constraintLayout);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Generates the PDF by calling the async task
     * @param v
     */
    public void startGenerate(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final GeneratePdfTask task = new GeneratePdfTask();
                task.execute();
            }
        }).start();
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

                    Toast.makeText(CreateAttestationActivity.this, "Attestation générée !", Toast.LENGTH_SHORT).show();

                    Intent show = new Intent(CreateAttestationActivity.this, MainActivity.class);

                    startActivity(show);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            generate();

            return null;
        }
    }

    /**
     * Save the attestation in DB
     * @return
     */
    private AttestationEntity saveInDb() {
        long id = dao.insert(new AttestationEntity(
                surname, currentDate, currentTime, null
        ));

        return dao.find(id);
    }

    /**
     * Get the PDF file name to open
     * @return PDF file name
     */
    private String getPdfFilename() {
        if (attestationType == AttestationType.AUTO_ATTESTATION_TRANSPORTS) {
            return "attestation-transports.pdf";
        }

        if (attestationType == AttestationType.ATTESTATION_DEPLACEMENT_DEROGATOIRE) {
            return "attestation.pdf";
        }

        return "attestation-100km.pdf";
    }

    /**
     * Generates the PDF file and the QRCode
     */
    private void generate() {
        try {
            saveFields();

            AssetManager assetManager = getAssets();

            InputStream attestation = assetManager.open(getPdfFilename());

            reader = new PdfReader(attestation);

            AttestationEntity attestationEntity = saveInDb();
            long attestationId = attestationEntity.getId();

            stamper = new PdfStamper(reader, new FileOutputStream(getPdfPath(attestationId)));

            mediabox = reader.getPageSize(1);

            form = stamper.getAcroFields();

            fillForm();

            fillMotives();

            attestationEntity.setReason(motivesDatabase.toString());
            dao.update(attestationEntity);

            saveQrCode(attestationId);

            addSmallQrCode();

            addBigQrCode(attestationId);

            stamper.setFormFlattening(true);

            stamper.close();
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

        city = cityInput.getText().toString();

        edit.putString("city", city);

        postalCode = postalCodeInput.getText().toString();

        edit.putString("postalCode", postalCode);

        destinationCity = destinationCityInput.getText().toString();

        edit.putString("destinationCity", destinationCity);

        destinationDepartment = destinationDepartmentInput.getText().toString();

        edit.putString("destinationDepartment", destinationDepartment);

        edit.apply();

        // Do not save this for further uses
        travelDate = travelDateInput.getText().toString();
        travelHour = travelHourInput.getText().toString();

        String[] hourMinute = travelHour.split("h");

        hour = hourMinute[0];
        minute = hourMinute[1];

        dao = AttestationDatabase.getInstance(CreateAttestationActivity.this).daoAccess();
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

        currentDay = String.format("%02d", day);
        currentMonth = String.format("%02d", month);

        currentDate = currentDay + '/' + currentMonth + '/' + String.format("%02d", year);

        travelDateInput.setText(currentDate);

        currentTime = String.format("%02d", currentHour) + "h" + String.format("%02d", currentMinute);

        travelHourInput.setText(currentTime);
    }

    /**
     * Fill the PDF form
     * @throws IOException
     * @throws DocumentException
     */
    public void fillForm() throws IOException, DocumentException {
        String fullName = surname + " " + lastName;

        if (attestationType == AttestationType.DECLARATION_DEPLACEMENT) {
            form.setField("Nom", lastName);
            form.setField("Prénom", surname);
            form.setField("Date Lieu de naissance", birthDate + " à " + birthPlace);
            form.setField("Rue", address);
            form.setField("Code Postal Ville", postalCode + " " + city);
            form.setField("Jour déplacement", travelDate.substring(0, 2));
            form.setField("Mois déplacement", travelDate.substring(3, 5));
            form.setField("Ville", destinationCity);
            form.setField("Département", destinationDepartment);
            form.setField("Ville signature", city);
            form.setField("Jour Signature", currentDay);
            form.setField("Mois Signature", currentMonth);

            if (((CheckBox) findViewById(R.id.recurring)).isChecked()) {
                form.setField("Récurrent", "Oui");
            }
        } else {
            form.setField("Nom et prénom", fullName);
            form.setField("Signature", fullName);
            form.setField("Date de naissance", birthDate);
            form.setField("Lieu de naissance", birthPlace);
            form.setField("Adresse actuelle", getFullAddress());

            if (attestationType == AttestationType.ATTESTATION_DEPLACEMENT_DEROGATOIRE) {
                form.setField("Heure", hour);
                form.setField("Minute", minute);
            }

            form.setField("Ville", city);
            form.setField("Date", travelDate);
        }
    }

    /**
     * Fill the PDF motives
     */
    private void fillMotives() throws IOException, DocumentException {
        if (((CheckBox) findViewById(R.id.reason1)).isChecked()) {
            form.setField("Déplacements entre domicile et travail", "Oui");
            addMotive("travail");
        }

        if (((CheckBox) findViewById(R.id.reason2)).isChecked() && attestationType == AttestationType.ATTESTATION_DEPLACEMENT_DEROGATOIRE) {
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

        if (((CheckBox) findViewById(R.id.reason5)).isChecked() && attestationType == AttestationType.ATTESTATION_DEPLACEMENT_DEROGATOIRE) {
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

        if (((CheckBox) findViewById(R.id.reason8)).isChecked() && attestationType == AttestationType.AUTO_ATTESTATION_TRANSPORTS) {
            form.setField("Déplacements entre domicile et école", "Oui");
            addMotive("scolaire");
        }

        if (((CheckBox) findViewById(R.id.reason9)).isChecked() && attestationType == AttestationType.AUTO_ATTESTATION_TRANSPORTS) {
            form.setField("Convcation police", "Oui");
            addMotive("police");
        }
    }

    /**
     * Save the QR code file
     * @throws WriterException
     * @throws IOException
     */
    private void saveQrCode(long id) throws WriterException, IOException {
        Bitmap bitmapQrCode = this.generateQrCode(getQrCodeText(), 300, 300);

        File qrCodeFile = new File(getQrCodePath(id));

        FileOutputStream ostream = new FileOutputStream(qrCodeFile);

        bitmapQrCode.compress(Bitmap.CompressFormat.PNG, 92, ostream);

        ostream.close();
    }

    /**
     * Append the small QR code to page 1
     * @throws WriterException
     * @throws IOException
     * @throws DocumentException
     */
    private void addSmallQrCode() throws WriterException, IOException, DocumentException {
        Bitmap smallBitmapQrCode;
        if (attestationType == AttestationType.DECLARATION_DEPLACEMENT) {
            smallBitmapQrCode = this.generateQrCode(getQrCodeText(), 98, 98);
        } else {
            smallBitmapQrCode = this.generateQrCode(getQrCodeText(), 100, 100);
        }

        byte[] byteArray = convertBitmapToByteArray(smallBitmapQrCode);

        Image image = Image.getInstance(byteArray);

        if (attestationType == AttestationType.DECLARATION_DEPLACEMENT) {
            addImage(image, 1, mediabox.getWidth() - 163, 135);
            addText("Date de création:", 479, 130, 6);
            addText(currentDate + " à " + currentTime, 470, 124, 6);
        } else {
            addImage(image, 1, mediabox.getWidth() - 170, 155);
            addText("Date de création:", 464, 150, 7);
            addText(currentDate + " à " + currentTime, 455, 144, 7);
        }
    }

    /**
     * Append the big QR code to page 2
     * @throws IOException
     * @throws DocumentException
     */
    private void addBigQrCode(long id) throws IOException, DocumentException {
        // Insert page 2
        stamper.insertPage(reader.getNumberOfPages() + 1,
                reader.getPageSizeWithRotation(1));

        Image image = Image.getInstance(getQrCodePath(id));

        addImage(image, 2, 50, mediabox.getHeight() - 350);
    }

    /**
     * Add text to the PDF
     * @param text
     * @param x
     * @param y
     */
    private void addText(String text, float x, float y, int size) {
        Phrase phrase = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, size, BaseColor.BLACK));
        ColumnText.showTextAligned(stamper.getOverContent(1), Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    /**
     * Path of the QR code file
     * @return
     */
    public String getQrCodePath(long id) {
        return getApplicationContext().getFilesDir() + "/" + id + ".png";
    }

    /**
     * Path of the PDF file
     * @return
     */
    public String getPdfPath(long id) {
        return getApplicationContext().getFilesDir() + "/" + id + ".pdf";
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    public String getQrCodeText() {
        if (attestationType == AttestationType.DECLARATION_DEPLACEMENT) {
            return "Cree le: " + currentDate + " a " + currentTime + "\nNom: " + lastName + "\nPrenom: " + surname + "\n" +
                    "Naissance: " + birthDate + " a " + birthPlace + "\nAdresse: " + getFullAddress() + "\n" +
                    "Sortie: " + travelDate + " vers " + destinationCity + "(" + destinationDepartment + ")" +
                    "\nMotifs: " + motivesQrCode;
        }

        return "Cree le: " + currentDate + " a " + currentTime + "; Nom: " + lastName + "; Prenom: " + surname + "; " +
                "Naissance: " + birthDate + " a " + birthPlace + "; Adresse: " + getFullAddress() + "; " +
                "Sortie: " + travelDate + " a " + travelHour + "; Motifs: " + motivesQrCode;
    }

    /**
     * Convert bitmap to array
     * @param bitmap
     * @return
     */
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream streamQrCode = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 92, streamQrCode);
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
        if (motivesQrCode.length() != 0) {
            motivesQrCode.append("-");
            motivesDatabase.append(", ");
        }
        motivesQrCode.append(motive);

        // Capitalize first letter
        motivesDatabase.append(motive.substring(0, 1).toUpperCase()).append(motive.substring(1));
    }

    /**
     * Get the full address
     * @return
     */
    public String getFullAddress() {
        return String.format("%s %s %s", address, postalCode, city);
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
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
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
