package com.poupa.attestationdeplacement.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.poupa.attestationdeplacement.AttestationsFragment;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;
import com.poupa.attestationdeplacement.ui.AttestationAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadAttestationsFragmentTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<AttestationsFragment> weakActivity;
    private final View rootView;
    ListView listView;

    AttestationAdapter adapter;

    public LoadAttestationsFragmentTask(AttestationsFragment myActivity, View rootView) {
        this.weakActivity = new WeakReference<>(myActivity);
        this.rootView = rootView;
    }

    @Override
    public Void doInBackground(Void... params) {
        List<AttestationEntity> attestations = AppDatabase.getInstance(weakActivity.get().getContext()).attestationDao().getAll();

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