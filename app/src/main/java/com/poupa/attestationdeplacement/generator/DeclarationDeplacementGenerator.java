package com.poupa.attestationdeplacement.generator;

import android.content.Context;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class DeclarationDeplacementGenerator extends AttestationGenerator {
    public DeclarationDeplacementGenerator(Context context, Attestation attestation) {
        super(context, attestation);

        smallQrCodeSize = 98;
    }

    /**
     * Get the PDF file name to open
     * @return PDF file name
     */
    String getPdfFilename() {
        return "attestation-100km.pdf";
    }

    /**
     * Fill the PDF motives
     */
    protected void fillMotives() throws IOException, DocumentException {
        super.fillMotives();

        if (attestation.isReason8()) {
            form.setField("Déplacements entre domicile et école", "Oui");
            attestation.addMotive("scolaire");
        }

        if (attestation.isReason9()) {
            form.setField("Convcation police", "Oui");
            attestation.addMotive("police");
        }
    }

    /**
     * Fill the PDF form
     * @throws IOException
     * @throws DocumentException
     */
    protected void fillForm() throws IOException, DocumentException {
        form.setField("Nom", attestation.getLastName());
        form.setField("Prénom", attestation.getSurname());
        form.setField("Date Lieu de naissance", attestation.getBirthDate() + " à " + attestation.getBirthPlace());
        form.setField("Rue", attestation.getAddress());
        form.setField("Code Postal Ville", attestation.getPostalCode() + " " + attestation.getCity());
        form.setField("Jour déplacement", attestation.getTravelDate().substring(0, 2));
        form.setField("Mois déplacement", attestation.getTravelDate().substring(3, 5));
        form.setField("Ville", attestation.getDestinationCity());
        form.setField("Département", attestation.getDestinationDepartment());
        form.setField("Ville signature", attestation.getCity());

        form.setField("Jour Signature", currentDay);
        form.setField("Mois Signature", currentMonth);

        if (attestation.isRecurring()) {
            form.setField("Récurrent", "Oui");
        }
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    protected String getQrCodeText() {
        return "Cree le: " + attestation.getCurrentDate() + " a " + attestation.getCurrentTime() + "\nNom: " + attestation.getLastName() + "\nPrenom: " + attestation.getSurname() + "\n" +
                "Naissance: " + attestation.getBirthDate() + " a " + attestation.getBirthPlace() + "\nAdresse: " + attestation.getFullAddress() + "\n" +
                "Sortie: " + attestation.getTravelDate() + " vers " + attestation.getDestinationCity() + "(" + attestation.getDestinationDepartment() + ")" +
                "\nMotifs: " + attestation.getMotivesQrCode();
    }
}
