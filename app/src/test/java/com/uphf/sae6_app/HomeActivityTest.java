package com.uphf.sae6_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.uphf.sae6_app.ui.activities.HomeActivity;
import com.uphf.sae6_app.ui.activities.QuizLevelActivity;

@RunWith(RobolectricTestRunner.class)
public class HomeActivityTest {

    @Before
    public void clearPrefs() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void withoutLevelScore_cardsAreHidden() {
        HomeActivity activity = Robolectric.buildActivity(HomeActivity.class).setup().get();

        View grid = activity.findViewById(R.id.home_cards_grid);
        View hint = activity.findViewById(R.id.quiz_test_hint);

        assertEquals(View.GONE, grid.getVisibility());
        assertEquals(View.VISIBLE, hint.getVisibility());
    }

    @Test
    public void quizTestButton_startsQuizLevelActivity() {
        HomeActivity activity = Robolectric.buildActivity(HomeActivity.class).setup().get();

        activity.findViewById(R.id.btn_quiz_test).performClick();

        Intent nextIntent = org.robolectric.Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(nextIntent);
        assertEquals(QuizLevelActivity.class.getName(), nextIntent.getComponent().getClassName());
    }
}

