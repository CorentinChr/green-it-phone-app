package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ProgressActivityTest {

    @Before
    public void clearPrefs() {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void whenAveragesAreAboveThreshold_readyMessageIsShown() {
        Context context = RuntimeEnvironment.getApplication();
        ScoreStorage.addScore(context, ScoreStorage.KEY_HISTORY_QUIZ, 80, 5);
        ScoreStorage.addScore(context, ScoreStorage.KEY_HISTORY_LEVEL, 8, 5);

        ProgressActivity activity = Robolectric.buildActivity(ProgressActivity.class).setup().get();
        TextView ready = activity.findViewById(R.id.progress_ready);

        assertTrue(ready.getText().toString().contains("Prêt"));
    }
}

