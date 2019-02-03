package com.ocr.moodtracker.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Mood.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;

    public abstract MoodDao moodDao();

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context,
                    AppDatabase.class, "moods.db").allowMainThreadQueries().build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }
}
