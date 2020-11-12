
package com.poupa.attestationdeplacement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.poupa.attestationdeplacement.db.ProfileDatabase;
import com.poupa.attestationdeplacement.db.ProfileEntity;
import com.poupa.attestationdeplacement.ui.ProfileAdapter;

import java.lang.ref.WeakReference;
import java.util.List;


public class ProfilesFragment extends Fragment {

    private View rootView;

    public ProfilesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        loadProfiles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.profiles_fragment, container, false);

        View empty = rootView.findViewById(R.id.profiles_empty);
        ListView listView = rootView.findViewById(R.id.profiles_list);
        listView.setEmptyView(empty);

        loadProfiles();

        return rootView;
    }

    public void loadProfiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LoadProfileTask task = new LoadProfileTask(ProfilesFragment.this, rootView);
                task.execute();
            }
        }).start();
    }

    static class LoadProfileTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ProfilesFragment> weakActivity;
        private final View rootView;
        ListView listView;
        ProfileAdapter adapter;

        LoadProfileTask(ProfilesFragment myActivity, View rootView) {
            this.weakActivity = new WeakReference<>(myActivity);
            this.rootView = rootView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<ProfileEntity> profiles = ProfileDatabase.getInstance(weakActivity.get().getContext()).profileDao().getAll();

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

}