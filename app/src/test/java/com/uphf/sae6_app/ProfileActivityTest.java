package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ProfileActivityTest {

    @Before
    public void clearPrefs() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void saveProfile_persistsNameAndLevel() {
        ProfileActivity activity = Robolectric.buildActivity(ProfileActivity.class).setup().get();

        EditText editName = activity.findViewById(R.id.editTextName);
        Spinner spinnerLevel = activity.findViewById(R.id.spinnerLevel);

        editName.setText("Rafael");
        spinnerLevel.setSelection(1);
        activity.findViewById(R.id.buttonSave).performClick();

        SharedPreferences prefs = activity.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        assertEquals("Rafael", prefs.getString("user_name", ""));
        assertEquals(QuizLevelActivity.LEVEL_INTERMEDIATE, prefs.getString("user_level", ""));
    }
}

