
package com.poupa.attestationdeplacement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.poupa.attestationdeplacement.tasks.LoadProfilesFragmentTask;

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
                (new LoadProfilesFragmentTask(ProfilesFragment.this, rootView)).execute();
            }
        }).start();
    }
}