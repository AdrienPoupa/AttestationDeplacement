package com.poupa.attestationdeplacement.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;

import com.poupa.attestationdeplacement.CreateAttestationActivity;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.ProfileEntity;
import com.poupa.attestationdeplacement.db.ProfileViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadProfilesCreateAttestationTask extends AsyncTask<Void, Void, Void> {
    ProfileEntity profileEntity;

    private final WeakReference<CreateAttestationActivity> weakActivity;
    private ArrayAdapter<ProfileEntity> adapter;

    public LoadProfilesCreateAttestationTask(CreateAttestationActivity createAttestationActivity) {
        this.weakActivity = new WeakReference<>(createAttestationActivity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<ProfileEntity> profiles = AppDatabase.getInstance(weakActivity.get()).profileDao().getAll();

        adapter =
                new ArrayAdapter<>(
                        weakActivity.get(),
                        R.layout.dropdown_menu_popup_item,
                        profiles);

        // Load supplied profile
        int position = weakActivity.get().getIntent().getIntExtra("position_profile", -1);
        if (position != -1) {
            ProfileViewModel profileViewModel = new ProfileViewModel(weakActivity.get().getApplication());

            profileEntity = profileViewModel.find(position);
        } else if (profiles.size() > 0) {
            // Load default profile
            profileEntity = profiles.get(0);
        }

        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        AutoCompleteTextView autoCompleteTextView =
                weakActivity.get().findViewById(R.id.filled_exposed_dropdown);
        autoCompleteTextView.setAdapter(adapter);


        if (profileEntity != null) {
            // https://stackoverflow.com/a/23568337/11115846
            if (Build.VERSION.SDK_INT > 16) {
                autoCompleteTextView.setText(profileEntity.toString(), false);
            } else {
                ListAdapter adapter = autoCompleteTextView.getAdapter();
                autoCompleteTextView.setAdapter(null);
                autoCompleteTextView.setText(profileEntity.toString());
                autoCompleteTextView.setAdapter((ArrayAdapter) adapter);
            }

            weakActivity.get().fillFieldsFromProfile(profileEntity);
        }
    }
}
