package com.poupa.attestationdeplacement.generator;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import com.google.zxing.WriterException;
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
import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.AttestationDao;
import com.poupa.attestationdeplacement.db.AttestationEntity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class AttestationGenerator {
    private final Context context;
    Attestation attestation;

    private PdfStamper stamper;
    private PdfReader reader;
    AcroFields form;

    private AttestationDao dao;
    String currentDay;
    String currentMonth;
    private String currentDate;
    private String currentTime;
    private String pdfFilename;

    int smallQrCodeSize;

    public AttestationGenerator(Context context, Attestation attestation) {
        this.context = context;
        this.attestation = attestation;
        setDates();
        smallQrCodeSize = 100;
        pdfFilename = "attestation.pdf";
    }

    /**
     * Generates the PDF file and the QRCode
     */
    public void generate() {
        try {
            AssetManager assetManager = context.getAssets();

            InputStream attestationInputStream = assetManager.open(pdfFilename);

            reader = new PdfReader(attestationInputStream);

            dao = AppDatabase.getInstance(context).attestationDao();

            AttestationEntity attestationEntity = saveInDb();
            attestation.setId(attestationEntity.getId());

            stamper = new PdfStamper(reader, new FileOutputStream(getPdfPath(attestation.getId())));

            form = stamper.getAcroFields();

            fillForm();

            fillMotives();

            attestationEntity.setReason(attestation.getMotivesDatabase().toString());
            dao.update(attestationEntity);

            addQrCodes();

            stamper.setFormFlattening(true);

            stamper.close();
        } catch (IOException | WriterException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @throws DocumentException
     * @throws IOException
     * @throws WriterException
     */
    private void addQrCodes() throws DocumentException, IOException, WriterException {
        QrCodeGenerator qrCodeGenerator = new QrCodeGenerator(attestation, context.getFilesDir());

        Rectangle mediabox = reader.getPageSize(1);

        // Small QR Code
        Image smallQrCode = Image.getInstance(qrCodeGenerator.generateSmallQrCode(getQrCodeText(), smallQrCodeSize));

        addImage(smallQrCode, 1, mediabox.getWidth() - 156, 100);

        // Insert page 2
        stamper.insertPage(reader.getNumberOfPages() + 1,
                reader.getPageSizeWithRotation(1));

        // Big QR Code
        Image bigQrCode = Image.getInstance(qrCodeGenerator.generateBigQrCode(getQrCodeText()));

        addImage(bigQrCode, 2, 50, mediabox.getHeight() - 350);
    }

    /**
     * Add text to the PDF
     * @param text
     * @param x
     * @param y
     */
    protected void addText(String text, float x, float y, int size) {
        Phrase phrase = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, size, BaseColor.BLACK));
        ColumnText.showTextAligned(stamper.getOverContent(1), Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    /**
     * Save the attestation in DB
     * @return
     */
    private AttestationEntity saveInDb() {
        long id = dao.insert(new AttestationEntity(
                attestation.getSurname() + " " + attestation.getLastName(), currentDate, currentTime, null
        ));

        return dao.find(id);
    }

    /**
     * Bootstrap the dates
     */
    private void setDates() {
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

        currentTime = String.format("%02d", currentHour) + "h" + String.format("%02d", currentMinute);
    }

    /**
     * Path of the PDF file
     * @return
     */
    private String getPdfPath(long id) {
        return context.getFilesDir() + "/" + id + ".pdf";
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
    private void addImage(Image image, int page, float x, float y) throws DocumentException, IOException {
        PdfImage stream = new PdfImage(image, "", null);

        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);

        image.setDirectReference(ref.getIndirectReference());
        image.setAbsolutePosition(x, y);

        PdfContentByte over = stamper.getOverContent(page);
        over.addImage(image);
    }

    /**
     * Fill the PDF motives
     */
    protected void fillMotives() {
        if (attestation.isReason1()) {
            addText("x", 78, 578, 18);
            attestation.addMotive("travail");
        }

        if (attestation.isReason2()) {
            addText("x", 78, 533, 18);
            attestation.addMotive("achats");
        }

        if (attestation.isReason3()) {
            addText("x", 78, 477, 18);
            attestation.addMotive("sante");
        }

        if (attestation.isReason4()) {
            addText("x", 78, 435, 18);
            attestation.addMotive("famille");
        }

        if (attestation.isReason5()) {
            addText("x", 78, 396, 18);
            attestation.addMotive("handicap");
        }

        if (attestation.isReason6()) {
            addText("x", 78, 358, 18);
            attestation.addMotive("sport_animaux");
        }

        if (attestation.isReason7()) {
            addText("x", 78, 295, 18);
            attestation.addMotive("convocation");
        }

        if (attestation.isReason8()) {
            addText("x", 78, 255, 18);
            attestation.addMotive("missions");
        }

        if (attestation.isReason9()) {
            addText("x", 78, 211, 18);
            attestation.addMotive("enfants");
        }
    }

    /**
     * Fill the PDF form
     */
    protected void fillForm() {
        String fullName = attestation.getSurname() + " " + attestation.getLastName();

        addText(fullName, 119, 696, 11);
        addText(attestation.getBirthDate(), 119, 674, 11);
        addText(attestation.getBirthPlace(), 297, 674, 11);
        addText(attestation.getFullAddress(), 133, 652, 11);

        addText(attestation.getHour() + ':' + attestation.getMinute(), 264, 153, 11);

        addText(attestation.getCity(), 105, 177, 11);
        addText(attestation.getTravelDate(), 91, 153, 11);
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    protected String getQrCodeText() {
        return "Cree le: " + attestation.getCurrentDate() + " a " + attestation.getCurrentTime() +
                ";\n Nom: " + attestation.getLastName() + ";\n Prenom: " + attestation.getSurname() + ";\n " +
                "Naissance: " + attestation.getBirthDate() + " a " + attestation.getBirthPlace() +
                ";\n Adresse: " + attestation.getFullAddress() + ";\n " +
                "Sortie: " + attestation.getTravelDate() + " a " +
                attestation.getHour() + ":" + attestation.getMinute() + ";\n " +
                "Motifs: " + attestation.getMotivesQrCode();
    }
}
