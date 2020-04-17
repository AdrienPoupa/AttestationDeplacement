package com.poupa.attestationdeplacement;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File docsFolder = new File(getApplicationContext().getFilesDir(), "");

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        };

        File[] files = docsFolder.listFiles(textFilter);

        // Redirect to creation page if no attestation found
        if (files != null && files.length == 0) {
            goToCreateAttestation();
            return;
        }

        ArrayList<String> filesList = new ArrayList<>();
        if (files != null) {
            // https://stackoverflow.com/a/21534151
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });

            for (File file : files) {
                filesList.add(file.getName().replaceFirst("[.][^.]+$", ""));
            }
        }

        ListView listView = findViewById(R.id.file_list);

        AttestationAdapter adapter = new AttestationAdapter(filesList, this);

        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateAttestation();
            }
        });
    }

    /**
     * Start the create attestation activity
     */
    private void goToCreateAttestation() {
        Intent intent = new Intent(MainActivity.this, CreateAttestationActivity.class);
        MainActivity.this.startActivity(intent);
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
