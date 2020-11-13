package com.poupa.attestationdeplacement.tasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.poupa.attestationdeplacement.CreateAttestationActivity;
import com.poupa.attestationdeplacement.MainActivity;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.generator.AttestationGenerator;

import java.lang.ref.WeakReference;

public class GeneratePdfTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<CreateAttestationActivity> weakActivity;
    ProgressDialog nDialog;
    AttestationGenerator attestationGenerator;

    public GeneratePdfTask(CreateAttestationActivity createAttestationActivity, AttestationGenerator attestationGenerator) {
        this.weakActivity = new WeakReference<>(createAttestationActivity);
        this.attestationGenerator = attestationGenerator;
    }

    @Override
    protected void onPreExecute() {
        weakActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                nDialog = new ProgressDialog(weakActivity.get());
                nDialog.setMessage(weakActivity.get().getString(R.string.loading));
                nDialog.setTitle(weakActivity.get().getString(R.string.generating));
                nDialog.setIndeterminate(true);
                nDialog.setCancelable(false);
                nDialog.show();
            }
        });
    }

    @Override
    protected void onPostExecute(Void result) {
        weakActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                nDialog.dismiss();

                Toast.makeText(
                        weakActivity.get(),
                        weakActivity.get().getString(R.string.attestation_generated),
                        Toast.LENGTH_SHORT
                ).show();

                Intent show = new Intent(weakActivity.get(), MainActivity.class);

                weakActivity.get().startActivity(show);
            }
        });
    }

    @Override
    protected Void doInBackground(Void... voids) {
        weakActivity.get().saveFields();

        attestationGenerator.generate();

        return null;
    }
}
