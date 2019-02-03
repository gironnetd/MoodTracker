package com.ocr.moodtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Manage the mood of the day
 */
public class MoodActivity extends AppCompatActivity  {

    private float y1, y2;

    private ConstraintLayout clRootView;
    private ImageView ivMood;

//    int[] colors = new int[] {
//            R.color.faded_red,
//            R.color.warm_grey,
//            R.color.cornflower_blue_65,
//            R.color.light_sage,
//            R.color.banana_yellow};
//    int[] smileys = new int[] {
//            R.drawable.smiley_sad,
//            R.drawable.smiley_disappointed,
//            R.drawable.smiley_normal,
//            R.drawable.smiley_happy,
//            R.drawable.smiley_super_happy};

    int colors[];
    TypedArray smileys;

    private int currentPosition = 3;

    private SharedPreferences mSharedPreferences;

    private final String POSITION = "POSITION";
    private final String PREFERENCE_NAME = "shared_preferences";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        clRootView = findViewById(R.id.activity_mood_root_view);
        ivMood = findViewById(R.id.iv_mood);
        mSharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(POSITION);
        } else {
            currentPosition = mSharedPreferences.getInt(POSITION, currentPosition);
        }
        colors = getResources().getIntArray(R.array.colors);
        smileys = getResources().obtainTypedArray(R.array.smileys);
        changeMood(currentPosition);
    }

    /**
     * Manage the swipe gesture to change the mood of the day
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                y2 = event.getY();
                if (y1 < y2) {
                    if(currentPosition > 0) {
                        currentPosition --;
                        changeMood(currentPosition);
                        mSharedPreferences.edit().putInt(POSITION, currentPosition).apply();
                    }
                }
                if (y1 > y2) {
                    if(currentPosition < colors.length - 1) {
                        currentPosition ++;
                        changeMood(currentPosition);
                        mSharedPreferences.edit().putInt(POSITION, currentPosition).apply();
                    }
                }
                break;
        }
        return false;
    }

    public void changeMood(int currentPosition) {
        clRootView.setBackgroundColor(colors[currentPosition]);
        ivMood.setBackground(smileys.getDrawable(currentPosition));
    }
}

