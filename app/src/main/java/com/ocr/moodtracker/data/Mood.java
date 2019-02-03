package com.ocr.moodtracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "mood")
public class Mood {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "mood_status")
    public int moodStatus;

    @ColumnInfo(name = "comment")
    public String comment;
}
