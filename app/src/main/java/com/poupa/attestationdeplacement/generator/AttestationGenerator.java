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
import com.poupa.attestationdeplacement.dto.Attestation;
import com.poupa.attestationdeplacement.dto.Reason;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AttestationGenerator {
    protected final Context context;
    Attestation attestation;

    protected PdfStamper stamper;
    protected PdfReader reader;
    AcroFields form;

    protected AttestationDao dao;

    int smallQrCodeSize;

    protected QrCodeGenerator qrCodeGenerator;

    public AttestationGenerator(Context context, Attestation attestation) {
        this.context = context;
        this.attestation = attestation;
        smallQrCodeSize = 106;
    }

    /**
     * Generates the PDF file and the QRCode
     */
    public void generate() {
        try {
            AssetManager assetManager = context.getAssets();

            InputStream attestationInputStream = assetManager.open(attestation.getPdfFileName());

            reader = new PdfReader(attestationInputStream);

            dao = AppDatabase.getInstance(context).attestationDao();

            AttestationEntity attestationEntity = saveInDb();
            attestation.setId(attestationEntity.getId());

            stamper = new PdfStamper(reader, new FileOutputStream(getPdfPath(attestation.getId())));

            qrCodeGenerator = new QrCodeGenerator(attestation, context.getFilesDir());

            form = stamper.getAcroFields();

            fillForm();

            fillMotives();

            attestationEntity.setReason(attestation.getReasonsDatabase());
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
     * @throws DocumentException
     * @throws IOException
     * @throws WriterException
     */
    protected void addQrCodes() throws DocumentException, IOException, WriterException {
        addSmallQrCode();

        // Insert new page
        stamper.insertPage(reader.getNumberOfPages() + 1,
                reader.getPageSizeWithRotation(1));

        addBigQrCode();
    }

    protected String qrTitle1 = "QR-code contenant les informations";
    protected String qrTitle2 = "de votre attestation numérique";

    /**
     * @throws DocumentException
     * @throws IOException
     * @throws WriterException
     */
    protected void addSmallQrCode() throws DocumentException, IOException, WriterException {
        // Small QR Code
        addText(qrTitle1, 440, 140, 6, 1, BaseColor.WHITE);
        addText(qrTitle2, 440, 130, 6, 1, BaseColor.WHITE);

        Image smallQrCode = Image.getInstance(qrCodeGenerator.generateSmallQrCode(getQrCodeText(), smallQrCodeSize));

        addImage(smallQrCode, reader.getNumberOfPages(), 440, 122);
    }

    /**
     * @throws DocumentException
     * @throws IOException
     * @throws WriterException
     */
    protected void addBigQrCode() throws DocumentException, IOException, WriterException {
        Rectangle mediabox = reader.getPageSize(1);

        // Big QR Code
        addText(qrTitle1 + " " + qrTitle2, 50, mediabox.getHeight() - 70, 11, 2, BaseColor.WHITE);

        Image bigQrCode = Image.getInstance(qrCodeGenerator.generateBigQrCode(getQrCodeText()));

        addImage(bigQrCode, reader.getNumberOfPages(), 50, mediabox.getHeight() - 420);
    }

    /**
     * Add text to the PDF in page 1
     * @param text
     * @param x
     * @param y
     */
    protected void addText(String text, float x, float y) {
        addText(text, x, y, 11, 1, BaseColor.BLACK);
    }

    /**
     * Add text to the PDF in page 1
     * @param text
     * @param x
     * @param y
     */
    protected void addText(String text, float x, float y, int size, int page) {
        addText(text, x, y, size, page, BaseColor.BLACK);
    }

    /**
     * Add text to the PDF
     * @param text
     * @param x
     * @param y
     * @param page
     */
    protected void addText(String text, float x, float y, int size, int page, BaseColor color) {
        Phrase phrase = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, size, color));
        ColumnText.showTextAligned(stamper.getOverContent(page), Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    /**
     * Save the attestation in DB
     * @return
     */
    private AttestationEntity saveInDb() {
        long id = dao.insert(new AttestationEntity(
                attestation.getSurname() + " " + attestation.getLastName(), attestation.getTravelDate(), attestation.getTravelHour(), null
        ));

        return dao.find(id);
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
    protected void addImage(Image image, int page, float x, float y) throws DocumentException, IOException {
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
        for (Reason reason: attestation.getReasons()) {
            if (reason.isEnabled()) {
                addText("x", reason.getX(), reason.getY(), 12, reason.getPage());
            }
        }
    }

    /**
     * Fill the PDF form
     */
    protected abstract void fillForm();

    /**
     * Returns the text shown in the QRCode
     * @return qr code text
     */
    protected String getQrCodeText() {
        return "Cree le: " + attestation.getTravelDate() + " a " + attestation.getHour() + "h" + attestation.getMinute() + ";\n" +
                "Nom: " + attestation.getLastName() + ";\n" +
                "Prenom: " + attestation.getSurname() + ";\n" +
                "Naissance: " + attestation.getBirthDate() + " a " + attestation.getBirthPlace() + ";\n" +
                "Adresse: " + attestation.getFullAddress() + ";\n" +
                "Sortie: " + attestation.getTravelDate() + " a " + attestation.getHour() + ":" + attestation.getMinute() + ";\n" +
                "Motifs: " + attestation.getReasonsQrCode() + ";";
    }
}
