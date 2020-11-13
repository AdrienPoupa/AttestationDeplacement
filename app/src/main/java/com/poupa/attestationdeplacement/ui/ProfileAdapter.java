package com.poupa.attestationdeplacement.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.poupa.attestationdeplacement.CreateAttestationActivity;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.db.AppDatabase;
import com.poupa.attestationdeplacement.db.ProfileEntity;

import java.util.List;

public class ProfileAdapter extends BaseAdapter implements ListAdapter {
    private final List<ProfileEntity> list;
    private final Context context;

    public ProfileAdapter(List<ProfileEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ProfileEntity getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.list.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profile_list_layout, null);
        }
        ProfileEntity profileEntity = list.get(position);

        TextView profileTitleTextView = convertView.findViewById(R.id.profile_title);
        profileTitleTextView.setText(String.format("%s %s", profileEntity.getFirstname(), profileEntity.getLastname()));

        TextView birthDateTextView = convertView.findViewById(R.id.profile_birth_date);
        birthDateTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range, 0, 0, 0);
        birthDateTextView.setText(String.format("%s à %s", profileEntity.getBirthdate(), profileEntity.getBirthplace()));

        TextView addressTextView = convertView.findViewById(R.id.profile_address);
        addressTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
        addressTextView.setText(String.format("%s %s %s", profileEntity.getAddress(), profileEntity.getPostalcode(), profileEntity.getCity()));

        MaterialButton useBtn = convertView.findViewById(R.id.use_btn);
        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateAttestationActivity.class);
                intent.putExtra("position_profile", getItem(position).getId());
                context.startActivity(intent);
            }
        });

        MaterialButton deleteProfileBtn = convertView.findViewById(R.id.delete_profile);
        deleteProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.delete_profile)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ProfileEntity profileEntity = getItem(position);
                                list.remove(position);
                                notifyDataSetChanged();

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppDatabase.getInstance(context).profileDao().delete(profileEntity);
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return convertView;
    }
}
