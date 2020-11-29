package com.poupa.attestationdeplacement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.poupa.attestationdeplacement.tasks.LoadAttestationsFragmentTask;

public class AttestationsFragment extends Fragment {

    private View rootView;

    public AttestationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAttestations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.attestations_fragment, container, false);

        View empty = rootView.findViewById(R.id.attestations_empty);
        ListView listView = rootView.findViewById(R.id.attestations_list);
        listView.setEmptyView(empty);

        loadAttestations();

        return rootView;
    }

    private void loadAttestations() {
        new Thread(() -> (new LoadAttestationsFragmentTask(AttestationsFragment.this, rootView)).execute()).start();
    }
}