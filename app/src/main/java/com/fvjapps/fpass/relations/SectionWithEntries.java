package com.fvjapps.fpass.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fvjapps.fpass.entities.Entry;
import com.fvjapps.fpass.entities.Section;

import java.util.List;

public class SectionWithEntries {

    @Embedded
    public Section section;

    @Relation(
            parentColumn = "id",
            entityColumn = "section_id"
    )
    public List<Entry> entries;
}
