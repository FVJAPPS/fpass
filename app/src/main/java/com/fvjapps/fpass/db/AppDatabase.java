package com.fvjapps.fpass.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.fvjapps.fpass.dao.EntryDao;
import com.fvjapps.fpass.dao.MediaBucketDao;
import com.fvjapps.fpass.dao.SectionDao;
import com.fvjapps.fpass.entities.Entry;
import com.fvjapps.fpass.entities.MediaBucket;
import com.fvjapps.fpass.entities.Section;

@Database(
        entities = {Section.class, Entry.class, MediaBucket.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SectionDao sectionDao();
    public abstract EntryDao entryDao();
    public abstract MediaBucketDao mediaBucketDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "fpass_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
