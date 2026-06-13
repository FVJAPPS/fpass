package com.fvjapps.fpass.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.entities.Entry;
import com.fvjapps.fpass.util.PasswordGenerator;

public class EditEntryDialog extends DialogFragment {

    private static final String ARG_ENTRY = "entry";

    private AppDatabase database;
    private Entry entry;

    public static EditEntryDialog newInstance(Entry entry) {
        EditEntryDialog dialog = new EditEntryDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, entry);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            entry = (Entry) getArguments().getSerializable(ARG_ENTRY);
        }
        if (entry == null) {
            dismiss();
            return super.onCreateDialog(savedInstanceState);
        }

        database = ((FpassApplication) requireActivity().getApplication()).getDatabase();

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_entry, null);

        ((TextView) view.findViewById(R.id.text_title)).setText(R.string.edit_entry);
        EditText inputUsername = view.findViewById(R.id.input_username);
        EditText inputEmail = view.findViewById(R.id.input_email);
        EditText inputPassword = view.findViewById(R.id.input_password);

        inputUsername.setText(entry.getUsername());
        inputEmail.setText(entry.getEmail());
        inputPassword.setText(entry.getPassword());

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

            entry.setUsername(username);
            entry.setEmail(email);
            entry.setPassword(password);

            new Thread(() -> {
                database.entryDao().update(entry);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), R.string.entry_updated, Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            }).start();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }
}
