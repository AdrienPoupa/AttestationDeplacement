package com.poupa.attestationdeplacement.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.poupa.attestationdeplacement.R;
import com.poupa.attestationdeplacement.db.AttestationDatabase;
import com.poupa.attestationdeplacement.db.AttestationEntity;

import java.io.File;
import java.util.List;

public class AttestationAdapter extends BaseAdapter implements ListAdapter {
    private List<AttestationEntity> list;
    private Context context;

    public AttestationAdapter(List<AttestationEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AttestationEntity getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return list.get(pos).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attestation_list_layout, null);
        }

        MaterialCardView card = convertView.findViewById(R.id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable backgroundColor;
                backgroundColor = new ColorDrawable(Color.BLACK);
                backgroundColor.setAlpha(192); // Transparency
    
                final Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawable(backgroundColor);
                dialog.setContentView(R.layout.dialog_qrcode);
                dialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT); // Full screen

                ImageView img = dialog.findViewById(R.id.dialog_qrcode_img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Set QRCode image
                String fileName = getItemId(position) + ".png";
                img.setImageURI(AttestationAdapter.this.getUri(fileName));

                dialog.show();
            }
        });

        AttestationEntity attestationEntity = list.get(position);

        TextView listItemText = convertView.findViewById(R.id.attestation_title);
        listItemText.setText(attestationEntity.getReason());

        TextView userNameText = convertView.findViewById(R.id.attestation_user_name);
        userNameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0);
        userNameText.setText(attestationEntity.getFirstName());

        TextView dateTimeText = convertView.findViewById(R.id.attestation_date_time);
        dateTimeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time, 0, 0, 0);
        dateTimeText.setText(attestationEntity.getGenerationDate() + " à " + attestationEntity.getGenerationTime());

        MaterialButton deleteBtn = convertView.findViewById(R.id.delete_btn);
        MaterialButton sendBtn = convertView.findViewById(R.id.send_btn);
        MaterialButton pdfBtn = convertView.findViewById(R.id.pdf_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.delete_information)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final AttestationEntity attestationEntity = getItem(position);

                                String fileName = String.valueOf(attestationEntity.getId());

                                File pdfFile = new File(context.getFilesDir() + "/" + fileName + ".pdf");
                                pdfFile.delete();

                                File qrCodeFile = new File(context.getFilesDir() + "/" + fileName + ".png");
                                qrCodeFile.delete();

                                list.remove(position);

                                notifyDataSetChanged();

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        AttestationDatabase.getInstance(context).daoAccess().delete(attestationEntity);
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

        pdfBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Clicking on items
                String fileName = getItemId(position) + ".pdf";

                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri uri = getUri(fileName);

                intent.setDataAndType(uri, "application/pdf");

                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resInfoList.size() > 0) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // https://stackoverflow.com/a/32950381/11989865
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }
                    intent = Intent.createChooser(intent, "Open File");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.no_pdf_reader, Toast.LENGTH_LONG).show();
                }

                notifyDataSetChanged();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = getItemId(position) + ".pdf";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, AttestationAdapter.this.getUri(fileName));
                sendIntent.setType("application/pdf");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });

        return convertView;
    }

    /**
     * Get the file Uri
     * @param fileName
     * @return
     */
    private Uri getUri(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }
}