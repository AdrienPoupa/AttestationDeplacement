package com.poupa.attestationdeplacement.generator;

import android.content.Context;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class AttestationDeplacementDerogatoireGenerator extends AttestationGenerator {
    public AttestationDeplacementDerogatoireGenerator(Context context, Attestation attestation) {
        super(context, attestation);
    }

    /**
     * Get the PDF file name to open
     * @return PDF file name
     */
    String getPdfFilename() {
        return "attestation.pdf";
    }

    /**
     * Fill the PDF motives
     */
    protected void fillMotives() throws IOException, DocumentException {
        super.fillMotives();

        if (attestation.isReason2()) {
            form.setField("Déplacements achats nécéssaires", "Oui");
            attestation.addMotive("courses");
        }

        if (attestation.isReason5()) {
            form.setField("Déplacements brefs (activité physique et animaux)", "Oui");
            attestation.addMotive("sport");
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

        form.setField("Heure", attestation.getHour());
        form.setField("Minute", attestation.getMinute());

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
