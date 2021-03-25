package com.poupa.attestationdeplacement.dto;

import android.content.Context;

import com.poupa.attestationdeplacement.MainActivity;
import com.poupa.attestationdeplacement.R;

import java.util.ArrayList;

/**
 * Curfew Attestation Data
 */
public class QuarantineAttestation extends Attestation {

    protected void setupReasons() {
        Context context = MainActivity.getContext();

        this.reasons = new ArrayList<>();
        this.reasons.add(new Reason(context.getString(R.string.reason1_quarantine_smalltext), "sport", 60, 367));
        this.reasons.add(new Reason(context.getString(R.string.reason2_quarantine_smalltext), "achats", 60, 244));
        this.reasons.add(new Reason(context.getString(R.string.reason3_quarantine_smalltext), "enfants", 60, 161));
        this.reasons.add(new Reason(context.getString(R.string.reason4_quarantine_smalltext), "culte_culturel", 60, 781, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason5_quarantine_smalltext), "demarche", 60, 726, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason6_quarantine_smalltext), "travail", 60, 629, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason7_quarantine_smalltext), "sante", 60, 533, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason8_quarantine_smalltext), "famille", 60, 477, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason9_quarantine_smalltext), "handicap", 60, 422, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason10_quarantine_smalltext), "judiciaire", 60, 380, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason11_quarantine_smalltext), "demenagement", 60, 311, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason12_quarantine_smalltext), "transits", 60, 243, 2));
    }

    @Override
    public String getPdfFileName() {
        return "quarantine-certificate.pdf";
    }

    @Override
    public String getReasonPrefix() {
        return "quarantine";
    }

    @Override
    public String toString() {
        Context context = MainActivity.getContext();

        return context.getString(R.string.quarantine_attestation);
    }
}
