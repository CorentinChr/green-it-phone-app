package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class QuizLevelResultActivityTest {

    @Before
    public void clearPrefs() {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void onCreate_displaysStoredScoreAndLevel() {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(QuizLevelActivity.KEY_USER_SCORE_10, 8)
                .putString(QuizLevelActivity.KEY_USER_LEVEL, QuizLevelActivity.LEVEL_ADVANCED)
                .commit();

        QuizLevelResultActivity activity = Robolectric.buildActivity(QuizLevelResultActivity.class).setup().get();

        TextView scoreTv = activity.findViewById(R.id.level_result_score);
        TextView levelTv = activity.findViewById(R.id.level_result_level);
        assertTrue(scoreTv.getText().toString().contains("8 / 10"));
        assertTrue(levelTv.getText().toString().contains("Difficile"));
    }

    @Test
    public void onCreate_withoutStoredResultShowsFallbackMessage() {
        QuizLevelResultActivity activity = Robolectric.buildActivity(QuizLevelResultActivity.class).setup().get();

        TextView messageTv = activity.findViewById(R.id.level_result_message);
        assertTrue(messageTv.getText().toString().contains("Aucun résultat trouvé"));
    }

    @Test
    public void backButton_finishesActivity() {
        QuizLevelResultActivity activity = Robolectric.buildActivity(QuizLevelResultActivity.class).setup().get();

        Button backBtn = activity.findViewById(R.id.level_result_back_btn);
        backBtn.performClick();

        assertTrue(activity.isFinishing());
    }
}

