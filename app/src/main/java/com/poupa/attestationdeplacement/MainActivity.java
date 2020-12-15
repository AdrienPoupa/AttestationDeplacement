package com.poupa.attestationdeplacement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.poupa.attestationdeplacement.ui.ViewPagerAdapter;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static WeakReference<Context> context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = new WeakReference<>(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupFloatingActionButton();
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> goToCreateAttestation(MainActivity.this));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AttestationsFragment(), getString(R.string.certificates));
        adapter.addFragment(new ProfilesFragment(), getString(R.string.profiles));
        viewPager.setAdapter(adapter);
    }

    /**
     * Start the create attestation activity
     */
    private static void goToCreateAttestation(MainActivity mainActivity) {
        Intent intent = new Intent(mainActivity, CreateAttestationActivity.class);
        mainActivity.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showFirstRunDialog();
    }

    /**
     * https://stackoverflow.com/a/9044235
     */
    private void showFirstRunDialog() {
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
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Display the about dialog
     */
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
        builder.setPositiveButton(getString(android.R.string.ok), null);
        builder.show();
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

        switch (id) {
            case R.id.action_warning:
                getInformationDialog();
                break;

            case R.id.action_about:
                getAboutDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContext() {
        return context.get();
    }
}
