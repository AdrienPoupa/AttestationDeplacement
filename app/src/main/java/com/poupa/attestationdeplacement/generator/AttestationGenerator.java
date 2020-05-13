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
import com.poupa.attestationdeplacement.CreateAttestationActivity;
import com.poupa.attestationdeplacement.db.AttestationDao;
import com.poupa.attestationdeplacement.db.AttestationDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public abstract class AttestationGenerator {
    private Context context;
    Attestation attestation;

    private PdfStamper stamper;
    private PdfReader reader;
    AcroFields form;

    private AttestationDao dao;
    String currentDay;
    String currentMonth;
    private String currentDate;
    private String currentTime;

    int smallQrCodeSize;

    AttestationGenerator(Context context, Attestation attestation) {
        this.context = context;
        this.attestation = attestation;
        setDates();
        smallQrCodeSize = 100;
    }

    /**
     * Generates the PDF file and the QRCode
     */
    public void generate() {
        try {
            AssetManager assetManager = context.getAssets();

            InputStream attestationInputStream = assetManager.open(getPdfFilename());

            reader = new PdfReader(attestationInputStream);

            dao = AttestationDatabase.getInstance(context).daoAccess();

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

        if (attestation.getAttestationType() == CreateAttestationActivity.AttestationType.DECLARATION_DEPLACEMENT) {
            addImage(smallQrCode, 1, mediabox.getWidth() - 163, 135);
            addText("Date de création:", 479, 130, 6);
            addText(attestation.getCurrentDate() + " à " + attestation.getCurrentTime(), 470, 124, 6);
        } else {
            addImage(smallQrCode, 1, mediabox.getWidth() - 170, 155);
            addText("Date de création:", 464, 150, 7);
            addText(attestation.getCurrentDate() + " à " + attestation.getCurrentTime(), 455, 144, 7);
        }

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
    private void addText(String text, float x, float y, int size) {
        Phrase phrase = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, size, BaseColor.BLACK));
        ColumnText.showTextAligned(stamper.getOverContent(1), Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    /**
     * Save the attestation in DB
     * @return
     */
    private AttestationEntity saveInDb() {
        long id = dao.insert(new AttestationEntity(
                attestation.getSurname(), currentDate, currentTime, null
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
    protected void fillMotives() throws IOException, DocumentException {
        if (attestation.isReason1()) {
            form.setField("Déplacements entre domicile et travail", "Oui");
            attestation.addMotive("travail");
        }

        if (attestation.isReason3()) {
            form.setField("Consultations et soins", "Oui");
            attestation.addMotive("sante");
        }

        if (attestation.isReason4()) {
            form.setField("Déplacements pour motif familial", "Oui");
            attestation.addMotive("famille");
        }

        if (attestation.isReason6()) {
            form.setField("Convcation judiciaire ou administrative", "Oui");
            attestation.addMotive("judiciaire");
        }

        if (attestation.isReason7()) {
            form.setField("Mission d'intérêt général", "Oui");
            attestation.addMotive("missions");
        }
    }

    /**
     * Fill the PDF form
     * @throws IOException
     * @throws DocumentException
     */
    abstract void fillForm() throws IOException, DocumentException;

    /**
     * Get the PDF file name to open
     * @return PDF file name
     */
    abstract String getPdfFilename();

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    abstract String getQrCodeText();
}
