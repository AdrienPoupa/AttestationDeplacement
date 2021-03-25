
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
        this.reasons.add(new Reason(context.getString(R.string.reason1_curfew_smalltext), "travail", 73, 579));
        this.reasons.add(new Reason(context.getString(R.string.reason2_curfew_smalltext), "sante", 73, 546));
        this.reasons.add(new Reason(context.getString(R.string.reason3_curfew_smalltext), "famille", 73, 512));
        this.reasons.add(new Reason(context.getString(R.string.reason4_curfew_smalltext), "handicap", 73, 478));
        this.reasons.add(new Reason(context.getString(R.string.reason5_curfew_smalltext), "convocation", 73, 458));
        this.reasons.add(new Reason(context.getString(R.string.reason6_curfew_smalltext), "missions", 73, 412));
        this.reasons.add(new Reason(context.getString(R.string.reason7_curfew_smalltext), "transits", 73, 379));
        this.reasons.add(new Reason(context.getString(R.string.reason8_curfew_smalltext), "animaux", 73, 345));
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
