package com.fvjapps.fpass.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fvjapps.fpass.entities.MediaBucket;

@Dao
public interface MediaBucketDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MediaBucket bucket);

    @Query("SELECT * FROM media_bucket WHERE hash = :hash")
    LiveData<MediaBucket> getByHash(String hash);

    @Query("SELECT * FROM media_bucket WHERE hash = :hash")
    MediaBucket getByHashSync(String hash);

    @Delete
    void delete(MediaBucket bucket);

    @Query("DELETE FROM media_bucket WHERE hash NOT IN " +
           "(SELECT DISTINCT icon_hash FROM sections WHERE icon_hash IS NOT NULL)")
    int deleteOrphans();
}
