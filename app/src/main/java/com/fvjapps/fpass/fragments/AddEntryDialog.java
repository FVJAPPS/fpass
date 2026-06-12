package com.fvjapps.fpass.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.entities.Entry;
import com.fvjapps.fpass.util.PasswordGenerator;

public class AddEntryDialog extends DialogFragment {

    private static final String ARG_SECTION_ID = "section_id";
    private int sectionId;
    private AppDatabase database;

    public static AddEntryDialog newInstance(int sectionId) {
        AddEntryDialog dialog = new AddEntryDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_ID, sectionId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionId = getArguments().getInt(ARG_SECTION_ID);
        }
        database = ((FpassApplication) requireActivity().getApplication()).getDatabase();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_entry, null);

        EditText inputUsername = view.findViewById(R.id.input_username);
        EditText inputEmail = view.findViewById(R.id.input_email);
        EditText inputPassword = view.findViewById(R.id.input_password);

        view.findViewById(R.id.btn_generate_password).setOnClickListener(v -> {
            inputPassword.setText(PasswordGenerator.generate());
        });

        view.findViewById(R.id.btn_save_entry).setOnClickListener(v -> {
            String username = inputUsername.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (username.isEmpty()) {
                inputUsername.setError("Required");
                return;
            }
            if (email.isEmpty()) {
                inputEmail.setError("Required");
                return;
            }
            if (password.isEmpty()) {
                inputPassword.setError("Required");
                return;
            }

            Entry entry = new Entry(0, sectionId, username, email, password);
            new Thread(() -> {
                database.entryDao().insert(entry);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), R.string.entry_created, Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            }).start();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }
}
