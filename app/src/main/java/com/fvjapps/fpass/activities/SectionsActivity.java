package com.fvjapps.fpass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.adapters.SectionAdapter;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.entities.Section;
import com.fvjapps.fpass.fragments.AddSectionDialog;
import com.fvjapps.fpass.fragments.EditSectionDialog;

public class SectionsActivity extends AppCompatActivity {

    private AppDatabase database;
    private SectionAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sections);

        database = ((FpassApplication) getApplication()).getDatabase();
        recyclerView = findViewById(R.id.recycler_sections);
        emptyText = findViewById(R.id.text_empty);

        adapter = new SectionAdapter(database.mediaBucketDao());
        recyclerView.setAdapter(adapter);

        adapter.setOnSectionEditListener(section -> {
            EditSectionDialog.newInstance(section).show(getSupportFragmentManager(), "edit_section");
        });

        adapter.setOnSectionDeleteListener(section -> {
            new Thread(() -> {
                database.sectionDao().delete(section);
                database.mediaBucketDao().deleteOrphans();
            }).start();
        });

        adapter.setOnSectionClickListener(section -> {
            Intent intent = new Intent(this, SectionDetailActivity.class);
            intent.putExtra("section_id", section.getId());
            intent.putExtra("section_name", section.getName());
            startActivity(intent);
        });

        database.sectionDao().getAllSectionsWithEntries().observe(this, sections -> {
            adapter.submitList(sections);
            boolean empty = sections == null || sections.isEmpty();
            recyclerView.setVisibility(empty ? android.view.View.GONE : android.view.View.VISIBLE);
            emptyText.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        findViewById(R.id.fab_add_section).setOnClickListener(v -> {
            new AddSectionDialog().show(getSupportFragmentManager(), "add_section");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAuthentication() {
        BiometricManager manager = BiometricManager.from(this);
        int canAuthenticate = manager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL
        );
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, R.string.auth_required, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
