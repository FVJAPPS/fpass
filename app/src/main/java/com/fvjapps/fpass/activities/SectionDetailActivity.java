package com.fvjapps.fpass.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.adapters.EntryAdapter;
import com.fvjapps.fpass.application.FpassApplication;
import com.fvjapps.fpass.db.AppDatabase;
import com.fvjapps.fpass.fragments.AddEntryDialog;

public class SectionDetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private EntryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private int sectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_section_detail);

        sectionId = getIntent().getIntExtra("section_id", -1);
        String sectionName = getIntent().getStringExtra("section_name");

        database = ((FpassApplication) getApplication()).getDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(sectionName != null ? sectionName : "Section");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_entries);
        emptyText = findViewById(R.id.text_empty);

        adapter = new EntryAdapter();
        recyclerView.setAdapter(adapter);

        database.entryDao().getEntriesBySectionId(sectionId).observe(this, entries -> {
            adapter.submitList(entries);
            boolean empty = entries == null || entries.isEmpty();
            recyclerView.setVisibility(empty ? android.view.View.GONE : android.view.View.VISIBLE);
            emptyText.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        findViewById(R.id.fab_add_entry).setOnClickListener(v -> {
            AddEntryDialog dialog = AddEntryDialog.newInstance(sectionId);
            dialog.show(getSupportFragmentManager(), "add_entry");
        });
    }
}
