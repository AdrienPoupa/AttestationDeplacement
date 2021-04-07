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
        this.reasons.add(new Reason(context.getString(R.string.reason1_quarantine_db_text), "travail", 31, 589));
        this.reasons.add(new Reason(context.getString(R.string.reason2_quarantine_db_text), "sante", 31, 511));
        this.reasons.add(new Reason(context.getString(R.string.reason3_quarantine_db_text), "famille", 31, 459));
        this.reasons.add(new Reason(context.getString(R.string.reason4_quarantine_db_text), "convocation_demarches", 31, 394));
        this.reasons.add(new Reason(context.getString(R.string.reason5_quarantine_db_text), "demenagement", 31, 328));
        this.reasons.add(new Reason(context.getString(R.string.reason6_quarantine_db_text), "achats_culte_culturel", 31, 264));
        this.reasons.add(new Reason(context.getString(R.string.reason7_quarantine_db_text), "sport", 31, 175));
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
