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
        mInstance = this;

        clRoot = findViewById(R.id.constraint_layout_root);
        clMoodYesterday = findViewById(R.id.cl_mood_1);
        clMoodTwoDaysAgo = findViewById(R.id.cl_mood_2);
        clMoodThreeDaysAgo = findViewById(R.id.cl_mood_3);
        clMoodFourDaysAgo = findViewById(R.id.cl_mood_4);
        clMoodFiveDaysAgo = findViewById(R.id.cl_mood_5);
        clMoodSixDaysAgo = findViewById(R.id.cl_mood_6);
        clMoodAWeekAgo = findViewById(R.id.cl_mood_7);

        clMoods = new ConstraintLayout[]{clMoodYesterday, clMoodTwoDaysAgo,
                clMoodThreeDaysAgo, clMoodFourDaysAgo, clMoodFiveDaysAgo,
                clMoodSixDaysAgo, clMoodAWeekAgo
        };

        ivCommentYesterday = findViewById(R.id.image_view_comment_1);
        ivCommentTwoDaysAgo = findViewById(R.id.image_view_comment_2);
        ivCommentThreeDaysAgo = findViewById(R.id.image_view_comment_3);
        ivCommentFourDaysAgo = findViewById(R.id.image_view_comment_4);
        ivCommentFiveDaysAgo = findViewById(R.id.image_view_comment_5);
        ivCommentSixaysAgo = findViewById(R.id.image_view_comment_6);
        ivCommentAWeekAgo = findViewById(R.id.image_view_comment_7);

        ivComments = new ImageView[]{ivCommentYesterday, ivCommentTwoDaysAgo,
                ivCommentThreeDaysAgo, ivCommentFourDaysAgo, ivCommentFiveDaysAgo,
                ivCommentSixaysAgo, ivCommentAWeekAgo
        };
        colors = getResources().getIntArray(R.array.colors);

    }

    /**
     * display all moods of the last seven days
     */
    public void displayMoods() {

        AppDatabase database = AppDatabase.getInMemoryDatabase(getApplicationContext());
        moods = database.moodDao().findAllMoods();

        if (moods.isEmpty()) {
            displayComment(getResources().getString(R.string.no_moods_register));
        } else {
            Collections.reverse(moods);
        }
        clRoot.requestLayout();
        clRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                final int width = size.x;

                for (int i = 0; i < moods.size(); i++) {
                    if (!moods.get(i).getComment().isEmpty() && !moods.get(i).getComment().equals("")) {
                        ivComments[i].setVisibility(View.VISIBLE);
                        ivComments[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayComment(moods.get(Integer.parseInt(v.getTag().toString())).getComment());
                            }
                        });
                    }

                    clMoods[i].setBackgroundColor(colors[moods.get(i).getMoodStatus()]);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) clMoods[i].getLayoutParams();
                    params.width = (int) (width * ((moods.get(i).getMoodStatus() + 1) * 2.0 / 10));
                    clMoods[i].setLayoutParams(params);
                    clMoods[i].setVisibility(View.VISIBLE);
                    if (i == moods.size() - 1) {
                        clRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMoods();
    }

    /**
     * show comment or message in a custom toast
     * @param comment
     */
    public void displayComment(String comment){
        Toast toast = Toast.makeText(HistoryActivity.this, comment, Toast.LENGTH_LONG);
        View toastView = toast.getView();

        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(18);
        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(24);
        toastView.setBackgroundColor(Color.BLACK);
        toastView.setPadding(24, 24, 24, 24);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toastView.setElevation(8.0f);
        }
        toast.show();
    }
}
