package com.poupa.attestationdeplacement;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;

public class AttestationAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;

    public AttestationAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attestation_list_layout, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show QR Code
                // https://stackoverflow.com/a/24946375
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //nothing;
                    }
                });

                // Set brightness to max
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.screenBrightness = 1;
                dialog.getWindow().setAttributes(lp);

                String fileName = getItem(position) + ".png";

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                ImageView image = new ImageView(context);
                image.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                image.setImageURI(AttestationAdapter.this.getUri(fileName));
                dialog.addContentView(image, new RelativeLayout.LayoutParams(
                        (4 * width)/7,
                        (2 * height)/5));
                dialog.show();
            }
        });

        TextView listItemText = convertView.findViewById(R.id.text1);
        listItemText.setText(list.get(position));

        ImageButton deleteBtn = convertView.findViewById(R.id.delete_btn);
        ImageButton sendBtn = convertView.findViewById(R.id.send_btn);
        ImageButton pdfBtn = convertView.findViewById(R.id.pdf_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.delete_information)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String fileName = (String) getItem(position);

                                File pdfFile = new File(context.getFilesDir() + "/" + fileName + ".pdf");
                                pdfFile.delete();

                                File qrCodeFile = new File(context.getFilesDir() + "/" + fileName + ".png");
                                qrCodeFile.delete();

                                list.remove(position);

                                notifyDataSetChanged();

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
                String fileName = getItem(position) + ".pdf";

                PackageManager pm = context.getPackageManager();

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(AttestationAdapter.this.getUri(fileName), "application/pdf");

                ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (info != null) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(AttestationAdapter.this.getUri(fileName), "application/pdf");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                String fileName = getItem(position) + ".pdf";

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

    private Uri getUri(String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(context.getFilesDir(), fileName);
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        }

        String filePath = context.getFilesDir() + "/" + fileName;

        return Uri.parse(filePath);
    }
}