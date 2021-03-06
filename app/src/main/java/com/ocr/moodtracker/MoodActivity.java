package com.ocr.moodtracker;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.ocr.moodtracker.receiver.AlarmBroadcastReceiver;

import static com.ocr.moodtracker.utils.Constants.CURRENT_COMMENT;
import static com.ocr.moodtracker.utils.Constants.DEFAULT_COMMENT;
import static com.ocr.moodtracker.utils.Constants.DEFAULT_MOOD;
import static com.ocr.moodtracker.utils.Constants.MOOD_STATUS;
import static com.ocr.moodtracker.utils.Constants.PREFERENCE_NAME;

/**
 * Manage the mood of the day
 */
public class MoodActivity extends AppCompatActivity  {

    private static MoodActivity mInstance;

    public static MoodActivity getInstance() {
        return mInstance;
    }

    private float y1, y2;

    private ConstraintLayout clRootView;
    private ImageView ivMood;

    int colors[];
    TypedArray smileys;
    int[] soundMoods;

    private int currentPosition = DEFAULT_MOOD;

    private SharedPreferences mSharedPreferences;

    private MediaPlayer player;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MOOD_STATUS, currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        mInstance = this;

        clRootView = findViewById(R.id.activity_mood_root_view);
        ivMood = findViewById(R.id.iv_mood);
        mSharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(MOOD_STATUS);
        } else {
            currentPosition = mSharedPreferences.getInt(MOOD_STATUS, currentPosition);
        }
        colors = getResources().getIntArray(R.array.colors);
        smileys = getResources().obtainTypedArray(R.array.smileys);
        changeMood(currentPosition);

        soundMoods = new int[] { R.raw.sad, R.raw.disappointed,
                R.raw.normal, R.raw.happy, R.raw.very_happy};


        if(!AlarmBroadcastReceiver.isAlarmStarted) {
            AlarmBroadcastReceiver.scheduleAlarm(getBaseContext());
            AlarmBroadcastReceiver.isAlarmStarted = true;
        }
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
                        animateBackgroundColor(currentPosition, currentPosition - 1);
                        currentPosition --;
                        animateSmiley();
                        mSharedPreferences.edit().putInt(MOOD_STATUS, currentPosition).apply();
                    }
                }
                if (y1 > y2) {
                    if(currentPosition < colors.length - 1) {
                        animateBackgroundColor(currentPosition, currentPosition + 1);
                        currentPosition ++;
                        animateSmiley();
                        mSharedPreferences.edit().putInt(MOOD_STATUS, currentPosition).apply();
                    }
                }
                break;
        }
        return false;
    }

    /**
     * Animate the change of the smiley
     */
    public void animateSmiley() {
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);
        animZoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeMood(currentPosition);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playSoundMood(currentPosition);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                ivMood.startAnimation(animZoomIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivMood.startAnimation(animZoomOut);
    }

    /**
     * Animate the background color from
     * @param currentPosition current mood color
     * @param toPosition new mood color
     */
    public void animateBackgroundColor(int currentPosition, int toPosition) {
        ObjectAnimator animator = ObjectAnimator.ofInt(clRootView, "backgroundColor", colors[currentPosition], colors[toPosition]).setDuration(1000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }

    /**
     * display the mood
     * @param currentPosition
     */
    public void changeMood(int currentPosition) {
        if(clRootView != null) clRootView.setBackgroundColor(colors[currentPosition]);
        if(ivMood != null) ivMood.setBackground(smileys.getDrawable(currentPosition));
    }

    /**
     * open the alert dialog to write comment for the day from comment button
     * @param view
     */
    public void writeComment(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View inflatedView = inflater.inflate(R.layout.dialog_comment, null);
        final EditText editText = inflatedView.findViewById(R.id.edit_text_comment);

        editText.append(mSharedPreferences.getString(CURRENT_COMMENT, DEFAULT_COMMENT));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       mSharedPreferences.edit().putString(CURRENT_COMMENT, editText.getText().toString()).apply();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    /**
     * play the sound associate with mood
     */
    public void playSoundMood(int currentPosition) {
        if(player != null) {
            player.reset();
            player.release();
        }

        player = MediaPlayer.create(getBaseContext(), soundMoods[currentPosition]);
        player.start();
    }

    public void startHistory(View view) {
        startActivity(new Intent(this, HistoryActivity.class));
    }
}

