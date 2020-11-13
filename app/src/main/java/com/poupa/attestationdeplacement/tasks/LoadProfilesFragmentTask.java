package com.poupa.attestationdeplacement.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.poupa.attestationdeplacement.ProfilesFragment;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.ProfileEntity;
import com.poupa.attestationdeplacement.ui.ProfileAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadProfilesFragmentTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<ProfilesFragment> weakActivity;
    private final View rootView;
    ListView listView;

    ProfileAdapter adapter;

    public LoadProfilesFragmentTask(ProfilesFragment myActivity, View rootView) {
        this.weakActivity = new WeakReference<>(myActivity);
        this.rootView = rootView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<ProfileEntity> profiles = AppDatabase.getInstance(weakActivity.get().getContext()).profileDao().getAll();

        listView = rootView.findViewById(R.id.profiles_list);

        adapter = new ProfileAdapter(profiles, weakActivity.get().getContext());

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