package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.uphf.sae6_app.ui.activities.QuizLevelActivity;

@RunWith(RobolectricTestRunner.class)
public class QuizLevelActivityTest {

    @Before
    public void clearPrefs() {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void displayCurrent_setsFirstQuestionProgress() {
        QuizLevelActivity activity = Robolectric.buildActivity(QuizLevelActivity.class).setup().get();
        android.widget.TextView progress = activity.findViewById(R.id.level_progress);

        assertTrue(progress.getText().toString().contains("1 / 10"));
    }

    @Test
    public void finishAndSave_storesAdvancedLevelForHighScore() throws Exception {
        QuizLevelActivity activity = Robolectric.buildActivity(QuizLevelActivity.class).setup().get();

        Field scoreField = QuizLevelActivity.class.getDeclaredField("score");
        scoreField.setAccessible(true);
        scoreField.set(activity, 9);

        Method finishAndSave = QuizLevelActivity.class.getDeclaredMethod("finishAndSave");
        finishAndSave.setAccessible(true);
        finishAndSave.invoke(activity);

        SharedPreferences prefs = activity.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        assertTrue(prefs.getBoolean(QuizLevelActivity.KEY_LEVEL_DONE, false));
        assertEquals(9, prefs.getInt(QuizLevelActivity.KEY_USER_SCORE_10, -1));
        assertEquals(QuizLevelActivity.LEVEL_ADVANCED, prefs.getString(QuizLevelActivity.KEY_USER_LEVEL, ""));
        assertNotNull(prefs.getString(QuizLevelActivity.KEY_USER_ID, null));
    }
}

