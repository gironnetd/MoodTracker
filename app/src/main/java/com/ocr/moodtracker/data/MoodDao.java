package com.ocr.moodtracker.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MoodDao {

    @Insert(onConflict = REPLACE)
    void insertMood(Mood mood);

    @Query("SELECT * FROM Mood")
    List<Mood> findAllMoods();
}
