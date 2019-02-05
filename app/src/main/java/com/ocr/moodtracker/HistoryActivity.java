package com.ocr.moodtracker;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ocr.moodtracker.data.AppDatabase;
import com.ocr.moodtracker.data.Mood;

import java.util.Collections;
import java.util.List;

/**
 * display the history of the last seven days mood recording
 */
public class HistoryActivity extends AppCompatActivity {

    private static HistoryActivity mInstance;

    public static HistoryActivity getInstance() {
        return mInstance;
    }

    ConstraintLayout clRoot, clMoodYesterday, clMoodTwoDaysAgo, clMoodThreeDaysAgo,
            clMoodFourDaysAgo, clMoodFiveDaysAgo, clMoodSixDaysAgo, clMoodAWeekAgo;

    ImageView ivCommentYesterday, ivCommentTwoDaysAgo, ivCommentThreeDaysAgo, ivCommentFourDaysAgo,
            ivCommentFiveDaysAgo, ivCommentSixaysAgo, ivCommentAWeekAgo;

    ConstraintLayout[] clMoods;
    ImageView[] ivComments;

    int colors[];
    List<Mood> moods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }
}
