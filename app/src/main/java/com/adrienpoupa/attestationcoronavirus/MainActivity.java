package com.adrienpoupa.attestationcoronavirus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.verifyStoragePermissions();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents/Attestations");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".pdf")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File files[] = docsFolder.listFiles(textFilter);

        ArrayList<String> filesList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            filesList.add(files[i].getName());
        }

        ListView lv = findViewById(R.id.filelist);

        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filesList));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                Intent intent;
                String fileName = (String) parent.getAdapter().getItem(position);
                String filePath = Environment.getExternalStorageDirectory() + "/Documents/Attestations/" + fileName;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File file = new File(filePath);
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filePath), "application/pdf");
                    intent = Intent.createChooser(intent, "Open File");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateAttestationActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * https://stackoverflow.com/a/5565700
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = findViewById(R.id.filelist);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * https://stackoverflow.com/a/33292700
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
