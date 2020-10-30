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

    protected void fillMotives() throws IOException, DocumentException {
        if (attestation.isReason1()) {
            form.setField("travail", "Oui");
            attestation.addMotive("travail");
        }

        if (attestation.isReason2()) {
            form.setField("achats", "Oui");
            attestation.addMotive("achats");
        }

        if (attestation.isReason3()) {
            form.setField("sante", "Oui");
            attestation.addMotive("sante");
        }

        if (attestation.isReason4()) {
            form.setField("famille", "Oui");
            attestation.addMotive("famille");
        }

        if (attestation.isReason5()) {
            form.setField("handicap", "Oui");
            attestation.addMotive("handicap");
        }

        if (attestation.isReason6()) {
            form.setField("sport_animaux", "Oui");
            attestation.addMotive("sport_animaux");
        }

        if (attestation.isReason7()) {
            form.setField("convocation", "Oui");
            attestation.addMotive("convocation");
        }

        if (attestation.isReason8()) {
            form.setField("missions", "Oui");
            attestation.addMotive("missions");
        }

        if (attestation.isReason9()) {
            form.setField("enfants", "Oui");
            attestation.addMotive("enfants");
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

        form.setField("HeureMinute", attestation.getHour() + ':' + attestation.getMinute());

        form.setField("Ville", attestation.getCity());
        form.setField("Date", attestation.getTravelDate());
    }

    /**
     * Returns the text shown in the QRCode
     * @return
     */
    protected String getQrCodeText() {
        return "Cree le: " + attestation.getCurrentDate() + " a " + attestation.getCurrentTime() + ";\n Nom: " + attestation.getLastName() + ";\n Prenom: " + attestation.getSurname() + ";\n " +
                "Naissance: " + attestation.getBirthDate() + " a " + attestation.getBirthPlace() + ";\n Adresse: " + attestation.getFullAddress() + ";\n " +
                "Sortie: " + attestation.getTravelDate() + " a " + attestation.getTravelHour() + ";\n Motifs: " + attestation.getMotivesQrCode();
    }
}
