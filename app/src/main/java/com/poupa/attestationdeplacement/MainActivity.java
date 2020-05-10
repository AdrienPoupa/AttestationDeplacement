package com.poupa.attestationdeplacement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.poupa.attestationdeplacement.db.AttestationDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final LoadAttestationsTask task = new LoadAttestationsTask(MainActivity.this);
                task.execute();
            }
        }).start();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateAttestation(MainActivity.this);
            }
        });
    }

    static class LoadAttestationsTask extends AsyncTask<Void, Void, Void> {
        // Weak references will still allow the Activity to be garbage-collected
        private final WeakReference<MainActivity> weakActivity;

        ListView listView;

        AttestationAdapter adapter;

        LoadAttestationsTask(MainActivity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        @Override
        public Void doInBackground(Void... params) {
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
