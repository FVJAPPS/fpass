package com.fvjapps.fpass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.adapters.SectionAdapter;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.entities.Section;
import com.fvjapps.fpass.fragments.AddSectionDialog;

public class SectionsActivity extends AppCompatActivity {

    private AppDatabase database;
    private SectionAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;

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
}
