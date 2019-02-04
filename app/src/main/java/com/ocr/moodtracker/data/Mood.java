package com.ocr.moodtracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

/**
 * represent the Mood object of a day
 */
@Entity(tableName = "mood")
public class Mood {

    @PrimaryKey
    private final int uid;

    @ColumnInfo(name = "since_today")
    private int sinceToday = 0;

    @ColumnInfo(name = "mood_status")
    private int moodStatus;

    @ColumnInfo(name = "comment")
    @Nullable
    private String comment;

    public Mood(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }


    public int getSinceToday() {
        return sinceToday;
    }

    public void setSinceToday(int sinceToday) {
        this.sinceToday = sinceToday;
    }

    public int getMoodStatus() {
        return moodStatus;
    }

    public void setMoodStatus(int moodStatus) {
        this.moodStatus = moodStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
