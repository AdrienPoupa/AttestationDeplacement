package com.poupa.attestationdeplacement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.poupa.attestationdeplacement.db.AttestationDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;
import com.poupa.attestationdeplacement.generator.Attestation;
import com.poupa.attestationdeplacement.generator.AttestationDeplacementDerogatoireGenerator;
import com.poupa.attestationdeplacement.ui.AttestationAdapter;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static  final String URI_SCHEME="attestation";
    public static final String URI_GENERATE = "/generate";
    public static final String URI_PARAM_SURNAME = "surname";
    public static final String URI_PARAM_LAST_NAME = "lastname";
    public static final String URI_PARAM_CITY = "city";
    public static final String URI_PARAM_POSTAL_CODE = "postalcode";
    public static final String URI_PARAM_ADDRESS = "address";
    public static final String URI_PARAM_BIRTH_PLACE = "birthplace";
    public static final String URI_PARAM_BIRTH_DATE = "birthdate";
    public static final String URI_PARAM_REASON_1 = "reason1";
    public static final String URI_PARAM_REASON_2 = "reason2";
    public static final String URI_PARAM_REASON_3 = "reason3";
    public static final String URI_PARAM_REASON_4 = "reason4";
    public static final String URI_PARAM_REASON_5 = "reason5";
    public static final String URI_PARAM_REASON_6 = "reason6";
    public static final String URI_PARAM_REASON_7 = "reason7";
    public static final String URI_PARAM_REASON_8 = "reason8";
    public static final String URI_PARAM_REASON_9 = "reason9";
    private ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadAttestations(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateAttestation(MainActivity.this);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadAttestations(false);
    }

    private void loadAttestations(final boolean firstrun) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LoadAttestationsTask task = new LoadAttestationsTask(MainActivity.this, firstrun);
                task.execute();
            }
        }).start();
    }

    static class LoadAttestationsTask extends AsyncTask<Void, Void, Void> {
        // Weak references will still allow the Activity to be garbage-collected
        private final WeakReference<MainActivity> weakActivity;
        // will try to create an attestion only if needed
        private final boolean firstrun;

        ListView listView;

        AttestationAdapter adapter;

        LoadAttestationsTask(MainActivity myActivity, boolean firstrun) {
            this.weakActivity = new WeakReference<>(myActivity);

            this.firstrun = firstrun;
        }

        @Override
        public Void doInBackground(Void... params) {

            if(firstrun) {
                weakActivity.get().createAttestationIfRequested();
            }

            List<AttestationEntity> attestations = AttestationDatabase.getInstance(weakActivity.get()).daoAccess().loadAll();

            if (attestations != null && attestations.size() == 0) {
                goToCreateAttestation(weakActivity.get());
                return null;
            }

            listView = weakActivity.get().findViewById(R.id.file_list);

            adapter = new AttestationAdapter(attestations, weakActivity.get());

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            if (listView != null) {
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }


    public void createAttestationIfRequested() {
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }

        Uri shortcut = intent.getData();
        if(shortcut == null) {
            return;
        }

        if (MainActivity.URI_GENERATE.equals(shortcut.getPath())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                String shortcutId = shortcut.toString();
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                shortcutManager.reportShortcutUsed(shortcutId);
            }

            Attestation attestation = new Attestation();
            attestation.setSurname(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_SURNAME)));
            attestation.setLastName(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_LAST_NAME)));
            attestation.setBirthDate(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_BIRTH_DATE)));
            attestation.setBirthPlace(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_BIRTH_PLACE)));
            attestation.setAddress(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_ADDRESS)));
            attestation.setCity(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_CITY)));
            attestation.setPostalCode(emptyStringIfNull(shortcut.getQueryParameter(URI_PARAM_POSTAL_CODE)));
            attestation.setReason1(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_1)));
            attestation.setReason2(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_2)));
            attestation.setReason3(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_3)));
            attestation.setReason4(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_4)));
            attestation.setReason5(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_5)));
            attestation.setReason6(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_6)));
            attestation.setReason7(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_7)));
            attestation.setReason8(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_8)));
            attestation.setReason9(Boolean.parseBoolean(shortcut.getQueryParameter(URI_PARAM_REASON_9)));

            Calendar mcurrentTime = Calendar.getInstance();
            attestation.setTravelDate(String.format("%1$td/%1$tm/%1$ty", mcurrentTime));
            attestation.setTravelHour(String.format("%1$tH/%1$tM", mcurrentTime));
            attestation.setHour(String.format("%1$tH", mcurrentTime));
            attestation.setMinute(String.format("%1$tM", mcurrentTime));
            attestation.setCurrentDate(String.format("%1$td/%1$tm/%1$ty", mcurrentTime));
            attestation.setCurrentTime(String.format("%1$tHh%1$tM", mcurrentTime));

            this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    nDialog = new ProgressDialog(MainActivity.this);
                    nDialog.setMessage(getString(R.string.loading));
                    nDialog.setTitle(getString(R.string.generating));
                    nDialog.setIndeterminate(true);
                    nDialog.setCancelable(false);
                    nDialog.show();
                }
            });

            AttestationDeplacementDerogatoireGenerator attestationGenerator = new AttestationDeplacementDerogatoireGenerator(this, attestation);
            attestationGenerator.generate();

            this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    nDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Attestation générée !", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // should be replaced by Objects.toString(v, "") but require API 19 (current is 16)
    private String emptyStringIfNull(String v) {
        if(v != null) {
            return v;
        } else {
            return "";
        }
    }

    /**
     * Start the create attestation activity
     */
    private static void goToCreateAttestation(MainActivity mainActivity) {
        Intent intent = new Intent(mainActivity, CreateAttestationActivity.class);
        mainActivity.startActivity(intent);
    }

    /**
     * https://stackoverflow.com/a/9044235
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        // Show popup once
        // https://androidwithdivya.wordpress.com/2017/02/14/how-to-launch-an-activity-only-once-for-the-first-time/
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            getInformationDialog();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
    }

    /**
     * Display warning dialog
     */
    private void getInformationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.information)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void getAboutDialog() {
        TextView tvVersion;
        TextView tvTitle;
    
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_about, null);
    
        tvTitle = dialogLayout.findViewById(R.id.about_tv_title);
        tvTitle.setText(getString(R.string.app_name));
        
        tvVersion = dialogLayout.findViewById(R.id.about_tv_version);
        tvVersion.setText(getString(R.string.version_number__1p, BuildConfig.VERSION_NAME));
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(android.R.string.ok),null);
        builder.show();
    }
    
    /**
     * https://stackoverflow.com/a/5565700
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = findViewById(R.id.file_list);
        list.setEmptyView(empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_warning:
                getInformationDialog();
                break;
    
            case R.id.action_about:
                getAboutDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
