package com.ocr.moodtracker.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.*;

/**
 * The Data Access Object for the Mood class.
 */
@Dao
public interface MoodDao {

    @Insert(onConflict = REPLACE)
    void insertMood(Mood mood);

    @Update(onConflict = REPLACE)
    void updateMood(Mood mood);

    @Update
    void updateMoods(List<Mood> moods);

    @Query("SELECT * FROM Mood")
    List<Mood> findAllMoods();

    @Delete
    void deleteMood(Mood mood);
}
