package com.fvjapps.fpass.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sections")
public class Section {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    @ColumnInfo(name = "icon_hash")
    @Nullable
    private String iconHash;

    private int color;

    public Section(int id, String name, @Nullable String iconHash, int color) {
        this.id = id;
        this.name = name;
        this.iconHash = iconHash;
        this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Nullable
    public String getIconHash() { return iconHash; }
    public void setIconHash(@Nullable String iconHash) { this.iconHash = iconHash; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
}
