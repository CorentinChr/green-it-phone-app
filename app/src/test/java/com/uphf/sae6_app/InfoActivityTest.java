package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

import com.uphf.sae6_app.ui.activities.InfoActivity;

@RunWith(RobolectricTestRunner.class)
public class InfoActivityTest {

    @Before
    public void clearPrefs() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void getDifficultyFromUserLevel_returnsAdvancedWhenStored() throws Exception {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("level_done", true).putString("user_level", "advanced").commit();

        InfoActivity activity = Robolectric.buildActivity(InfoActivity.class).setup().get();
        Method m = InfoActivity.class.getDeclaredMethod("getDifficultyFromUserLevel");
        m.setAccessible(true);

        int difficulty = (int) m.invoke(activity);
        assertEquals(3, difficulty);
    }
}

