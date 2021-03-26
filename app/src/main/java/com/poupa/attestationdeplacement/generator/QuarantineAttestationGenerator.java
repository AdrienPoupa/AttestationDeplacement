package com.poupa.attestationdeplacement.generator;

import android.content.Context;

import com.poupa.attestationdeplacement.dto.QuarantineAttestation;

public class QuarantineAttestationGenerator extends AttestationGenerator {

    public QuarantineAttestationGenerator(Context context, QuarantineAttestation attestation) {
        super(context, attestation);
    }

    /**
     * Fill the PDF form
     */
    protected void fillForm() {
        String fullName = attestation.getSurname() + " " + attestation.getLastName();

        addText(fullName, 111, 516);
        addText(attestation.getBirthDate(), 111, 501);
        addText(attestation.getBirthPlace(), 228, 501);
        addText(attestation.getFullAddress(), 126, 487);

        addText("Fait à" + attestation.getCity(), 72, 109, 11, 2);
        addText("Le " + attestation.getTravelDate(), 72, 93, 11, 2);
        addText("à " + attestation.getHour() + ':' + attestation.getMinute(), 310, 93, 11, 2);
        addText("(Date et heure de début de sortie à mentionner obligatoirement)", 72, 77, 11, 2);
    }
}
