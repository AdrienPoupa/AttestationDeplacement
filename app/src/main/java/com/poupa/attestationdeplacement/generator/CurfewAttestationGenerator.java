package com.poupa.attestationdeplacement.generator;

import android.content.Context;

import com.poupa.attestationdeplacement.dto.CurfewAttestation;

public class CurfewAttestationGenerator extends AttestationGenerator {

    public CurfewAttestationGenerator(Context context, CurfewAttestation attestation) {
        super(context, attestation);
    }

    /**
     * Fill the PDF form
     */
    protected void fillForm() {
        String fullName = attestation.getSurname() + " " + attestation.getLastName();

        addText(fullName, 144, 705);
        addText(attestation.getBirthDate(), 144, 684);
        addText(attestation.getBirthPlace(), 310, 684);
        addText(attestation.getFullAddress(), 148, 665);
    }
}
