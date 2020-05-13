package com.poupa.attestationdeplacement.generator;

import android.content.Context;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class AutoAttestationTransportsGenerator extends AttestationGenerator {
    public AutoAttestationTransportsGenerator(Context context, Attestation attestation) {
        super(context, attestation);
    }

    /**
     * Get the PDF file name to open
     * @return PDF file name
     */
    String getPdfFilename() {
        return "attestation-transports.pdf";
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
        String fullName = attestation.getSurname() + " " + attestation.getLastName();

        form.setField("Nom et prénom", fullName);
        form.setField("Signature", fullName);
        form.setField("Date de naissance", attestation.getBirthDate());
        form.setField("Lieu de naissance", attestation.getBirthPlace());
        form.setField("Adresse actuelle", attestation.getFullAddress());

        form.setField("Ville", attestation.getCity());
        form.setField("Date", attestation.getTravelDate());
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    protected String getQrCodeText() {
        return "Cree le: " + attestation.getCurrentDate() + " a " + attestation.getCurrentTime() + ";Nom: " + attestation.getLastName() + ";Prenom: " + attestation.getSurname() + ";" +
                "Naissance: " + attestation.getBirthDate() + " a " + attestation.getBirthPlace() + ";Adresse: " + attestation.getFullAddress() + ";" +
                "Sortie: " + attestation.getTravelDate() + " a " + attestation.getTravelHour() + ";Motifs: " + attestation.getMotivesQrCode();
    }
}
