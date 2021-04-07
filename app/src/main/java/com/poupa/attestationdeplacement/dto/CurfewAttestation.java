
package com.poupa.attestationdeplacement.dto;

import android.content.Context;

import com.poupa.attestationdeplacement.MainActivity;
import com.poupa.attestationdeplacement.R;

import java.util.ArrayList;

/**
 * Curfew Attestation Data
 */
public class CurfewAttestation extends Attestation {

    protected void setupReasons() {
        Context context = MainActivity.getContext();

        this.reasons = new ArrayList<>();
        this.reasons.add(new Reason(context.getString(R.string.reason1_curfew_db_text), "travail", 31, 589));
        this.reasons.add(new Reason(context.getString(R.string.reason2_curfew_db_text), "sante", 31, 511));
        this.reasons.add(new Reason(context.getString(R.string.reason3_curfew_db_text), "famille", 31, 459));
        this.reasons.add(new Reason(context.getString(R.string.reason4_curfew_db_text), "convocation_demarches", 31, 394));
        this.reasons.add(new Reason(context.getString(R.string.reason5_curfew_db_text), "animaux", 31, 328));
    }

    @Override
    public String getPdfFileName() {
        return "curfew-certificate.pdf";
    }

    @Override
    public String getReasonPrefix() {
        return "curfew";
    }

    @Override
    public String toString() {
        Context context = MainActivity.getContext();

        return context.getString(R.string.curfew_attestation);
    }
}
