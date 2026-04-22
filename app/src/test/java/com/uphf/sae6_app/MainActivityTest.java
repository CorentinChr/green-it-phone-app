package com.uphf.sae6_app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

import com.uphf.sae6_app.ui.activities.MainActivity;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @Test
    public void onCreate_setsMainRootView() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        assertNotNull(activity.findViewById(R.id.main));
    }
}

