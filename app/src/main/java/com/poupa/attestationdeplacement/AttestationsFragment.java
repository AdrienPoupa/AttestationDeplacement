package com.poupa.attestationdeplacement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;
import com.poupa.attestationdeplacement.ui.AttestationAdapter;

import java.lang.ref.WeakReference;
import java.util.List;


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

    private void loadAttestations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AttestationsFragment.LoadAttestationsTask task = new AttestationsFragment.LoadAttestationsTask(AttestationsFragment.this, rootView);
                task.execute();
            }
        }).start();
    }

    static class LoadAttestationsTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<AttestationsFragment> weakActivity;
        private final View rootView;
        ListView listView;

        AttestationAdapter adapter;

        LoadAttestationsTask(AttestationsFragment myActivity, View rootView) {
            this.weakActivity = new WeakReference<>(myActivity);
            this.rootView = rootView;
        }

        @Override
        public Void doInBackground(Void... params) {
            List<AttestationEntity> attestations = AppDatabase.getInstance(weakActivity.get().getContext()).attestationDao().loadAll();

            listView = rootView.findViewById(R.id.attestations_list);

            adapter = new AttestationAdapter(attestations, weakActivity.get().getContext());

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

}