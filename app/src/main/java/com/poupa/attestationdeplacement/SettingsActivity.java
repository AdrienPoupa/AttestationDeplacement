package com.poupa.attestationdeplacement;

import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            SwitchPreferenceCompat createShortcutPreference = getPreferenceManager().findPreference("create_shortcuts");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                createShortcutPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if((Boolean) newValue == false) {
                        ShortcutManager shortcutManager = requireActivity().getSystemService(ShortcutManager.class);
                        shortcutManager.removeAllDynamicShortcuts();
                    }
                    return true;
                });
            } else {
                createShortcutPreference.setSummaryOn(R.string.shortcuts_summary_disabled);
                createShortcutPreference.setSummaryOff(R.string.shortcuts_summary_disabled);
                createShortcutPreference.setEnabled(false);
            }

            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }
}