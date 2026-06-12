package com.fvjapps.fpass.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fvjapps.fpass.entities.Section;
import com.fvjapps.fpass.relations.SectionWithEntries;

import java.util.List;

@Dao
public interface SectionDao {

    @Insert
    long insert(Section section);

    @Update
    void update(Section section);

    @Delete
    void delete(Section section);

    @Query("SELECT * FROM sections ORDER BY name ASC")
    LiveData<List<Section>> getAllSections();

    @Query("SELECT * FROM sections WHERE id = :sectionId")
    LiveData<Section> getSectionById(int sectionId);

    @Transaction
    @Query("SELECT * FROM sections WHERE id = :sectionId")
    LiveData<SectionWithEntries> getSectionWithEntries(int sectionId);

    @Transaction
    @Query("SELECT * FROM sections ORDER BY name ASC")
    LiveData<List<SectionWithEntries>> getAllSectionsWithEntries();
}
