package com.fvjapps.fpass.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_bucket")
public class MediaBucket {

    @PrimaryKey
    @NonNull
    private String hash;

    @ColumnInfo(name = "base64_data")
    private String base64Data;

    @ColumnInfo(name = "mime_type")
    private String mimeType;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public MediaBucket(@NonNull String hash, String base64Data, String mimeType, long createdAt) {
        this.hash = hash;
        this.base64Data = base64Data;
        this.mimeType = mimeType;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getHash() { return hash; }
    public void setHash(@NonNull String hash) { this.hash = hash; }

    public String getBase64Data() { return base64Data; }
    public void setBase64Data(String base64Data) { this.base64Data = base64Data; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
