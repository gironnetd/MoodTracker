package com.ocr.moodtracker.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.ocr.moodtracker.data.AppDatabase;
import com.ocr.moodtracker.data.Mood;

import java.util.Calendar;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static com.ocr.moodtracker.utils.Constants.CURRENT_COMMENT;
import static com.ocr.moodtracker.utils.Constants.DEFAULT_COMMENT;
import static com.ocr.moodtracker.utils.Constants.DEFAULT_MOOD;
import static com.ocr.moodtracker.utils.Constants.MOOD_STATUS;
import static com.ocr.moodtracker.utils.Constants.PREFERENCE_NAME;

/**
 * Manage the recording of the mood of the day at midnight
 * in the database
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static boolean isAlarmStarted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Schedule alarm on BOOT_COMPLETED
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            scheduleAlarm(context);
        } else {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            AppDatabase database = AppDatabase.getInMemoryDatabase(context);
            List<Mood> moods = database.moodDao().findAllMoods();

            if (!moods.isEmpty()) {
                for (Mood mood : moods) { mood.setSinceToday(mood.getSinceToday() + 1); }
            }

            database.moodDao().updateMoods(moods);

            if (moods.size() == 7) {
                database.moodDao().deleteMood(moods.get(0));
            }

            Mood moodOfDay = new Mood(moods.size());
            moodOfDay.setMoodStatus(preferences.getInt(MOOD_STATUS, DEFAULT_MOOD));
            moodOfDay.setComment(preferences.getString(CURRENT_COMMENT, DEFAULT_COMMENT));

            database.moodDao().insertMood(moodOfDay);

            preferences.edit().putInt(MOOD_STATUS, DEFAULT_MOOD).apply();
            preferences.edit().putString(CURRENT_COMMENT, DEFAULT_COMMENT).apply();

            scheduleAlarm(context);
        }
    }

    /* Schedule the alarm based on user preferences */
    public static void scheduleAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(System.currentTimeMillis());
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
        } else {
            startTime.set(Calendar.HOUR_OF_DAY, 11);
            startTime.set(Calendar.MINUTE, 59);
            startTime.set(Calendar.SECOND, 59);
            startTime.set(Calendar.AM_PM, Calendar.PM);
        }

        if (Calendar.getInstance().after(startTime)) {
            startTime.add(Calendar.DATE, 1);
        }

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            manager.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), operation);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            manager.setExact(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), operation);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), operation);
        }
    }
}
