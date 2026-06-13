package com.fvjapps.fpass.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.fvjapps.fpass.R;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.entities.MediaBucket;
import com.fvjapps.fpass.entities.Section;
import com.fvjapps.fpass.util.HashUtils;
import com.fvjapps.fpass.util.ImageCompressor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditSectionDialog extends DialogFragment {

    private static final String ARG_SECTION = "section";

    private AppDatabase database;
    private Section section;
    private int selectedColor;
    private String iconHash;
    private String iconBase64;
    private String iconMimeType;
    private ImageView iconPreview;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) handleImageUri(uri);
            });

    public static EditSectionDialog newInstance(Section section) {
        EditSectionDialog dialog = new EditSectionDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION, section);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            section = (Section) getArguments().getSerializable(ARG_SECTION);
        }
        if (section == null) {
            dismiss();
            return super.onCreateDialog(savedInstanceState);
        }

        database = ((FpassApplication) requireActivity().getApplication()).getDatabase();
        selectedColor = section.getColor();
        iconHash = section.getIconHash();

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_section, null);

        ((TextView) view.findViewById(R.id.text_title)).setText(R.string.edit_section);
        EditText inputName = view.findViewById(R.id.input_name);
        inputName.setText(section.getName());
        iconPreview = view.findViewById(R.id.image_icon_preview);

        view.<View>findViewById(R.id.btn_pick_color).setBackgroundColor(selectedColor);
        view.findViewById(R.id.btn_pick_color).setOnClickListener(v -> {
            ColorPickerDialog picker = new ColorPickerDialog();
            picker.setOnColorSelectedListener(color -> {
                selectedColor = color;
                view.findViewById(R.id.btn_pick_color).setBackgroundColor(color);
            });
            picker.show(getParentFragmentManager(), "color_picker");
        });

        view.findViewById(R.id.btn_upload_icon).setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });

        if (iconHash != null && !iconHash.isEmpty()) {
            new Thread(() -> {
                MediaBucket bucket = database.mediaBucketDao().getByHashSync(iconHash);
                if (bucket != null && bucket.getBase64Data() != null) {
                    byte[] bytes = Base64.decode(bucket.getBase64Data(), Base64.NO_WRAP);
                    requireActivity().runOnUiThread(() -> {
                        iconPreview.setVisibility(View.VISIBLE);
                        Glide.with(EditSectionDialog.this).load(bytes).override(96, 96).into(iconPreview);
                    });
                }
            }).start();
        }

        view.findViewById(R.id.btn_save_section).setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            if (name.isEmpty()) {
                inputName.setError("Name is required");
                return;
            }
            new Thread(() -> {
                if (iconHash != null && iconBase64 != null) {
                    MediaBucket bucket = new MediaBucket(
                            iconHash, iconBase64, iconMimeType, System.currentTimeMillis());
                    database.mediaBucketDao().insert(bucket);
                }
                section.setName(name);
                section.setIconHash(iconHash);
                section.setColor(selectedColor);
                database.sectionDao().update(section);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), R.string.section_updated, Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            }).start();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }

    private void handleImageUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();
            inputStream.close();

            imageBytes = ImageCompressor.compress(imageBytes);
            iconBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            iconHash = HashUtils.sha256(iconBase64);
            iconMimeType = requireContext().getContentResolver().getType(uri);

            iconPreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageBytes).override(96, 96).into(iconPreview);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }
}
