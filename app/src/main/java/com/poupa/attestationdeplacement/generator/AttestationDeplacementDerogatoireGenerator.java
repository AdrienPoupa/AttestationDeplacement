package com.poupa.attestationdeplacement.generator;

import android.content.Context;

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
