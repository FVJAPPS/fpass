package com.fvjapps.fpass.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "entries",
        foreignKeys = @ForeignKey(
                entity = Section.class,
                parentColumns = "id",
                childColumns = "section_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("section_id")
)
public class Entry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "section_id")
    private int sectionId;

    private String username;

    private String email;

    private String password;

    public Entry(int id, int sectionId, String username, String email, String password) {
        this.id = id;
        this.sectionId = sectionId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
