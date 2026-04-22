package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.uphf.sae6_app.data.local.ScoreStorage;
import com.uphf.sae6_app.ui.activities.QuizLevelActivity;

@RunWith(RobolectricTestRunner.class)
public class ScoreStorageTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void addScore_keepsMostRecentAndMaxSize() {
        for (int i = 1; i <= 6; i++) {
            ScoreStorage.addScore(context, ScoreStorage.KEY_HISTORY_QUIZ, i, 5);
        }

        List<Integer> scores = ScoreStorage.getScores(context, ScoreStorage.KEY_HISTORY_QUIZ);
        assertEquals(5, scores.size());
        assertEquals(Integer.valueOf(6), scores.get(0));
        assertEquals(Integer.valueOf(2), scores.get(4));
    }

    @Test
    public void average_handlesNullValues() {
        double avg = ScoreStorage.average(Arrays.asList(10, null, 20));
        assertEquals(10.0, avg, 0.0001);
    }

    @Test
    public void formatList_returnsReadableLines() {
        String formatted = ScoreStorage.formatList(Arrays.asList(80, 70), "%");
        assertTrue(formatted.contains("1. 80%"));
        assertTrue(formatted.contains("2. 70%"));
    }
}

