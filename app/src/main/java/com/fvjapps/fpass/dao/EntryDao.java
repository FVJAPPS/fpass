package com.fvjapps.fpass.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fvjapps.fpass.entities.Entry;

import java.util.List;

@Dao
public interface EntryDao {

    @Insert
    long insert(Entry entry);

    @Update
    void update(Entry entry);

    @Delete
    void delete(Entry entry);

    @Query("SELECT * FROM entries WHERE section_id = :sectionId ORDER BY username ASC")
    LiveData<List<Entry>> getEntriesBySectionId(int sectionId);

    @Query("SELECT * FROM entries WHERE id = :entryId")
    LiveData<Entry> getEntryById(int entryId);
}
