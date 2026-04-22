package com.uphf.sae6_app;

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.uphf.sae6_app.ui.activities.DashboardActivity;

@RunWith(RobolectricTestRunner.class)
public class DashboardActivityTest {

    @Test
    public void estimateServicesKg_returnsExpectedValues() throws Exception {
        DashboardActivity activity = Robolectric.buildActivity(DashboardActivity.class).setup().get();
        Method method = DashboardActivity.class.getDeclaredMethod("estimateServicesKg", int.class, String.class);
        method.setAccessible(true);

        double sd = (double) method.invoke(activity, 10, "SD");
        double hd = (double) method.invoke(activity, 10, "HD");
        double uhd = (double) method.invoke(activity, 10, "UHD");

        assertEquals(31.2, sd, 0.0001);
        assertEquals(93.6, hd, 0.0001);
        assertEquals(218.4, uhd, 0.0001);
    }

    @Test
    public void recalcImpact_updatesImpactText() throws Exception {
        DashboardActivity activity = Robolectric.buildActivity(DashboardActivity.class).setup().get();
        Method method = DashboardActivity.class.getDeclaredMethod("recalcImpact", int.class, boolean.class);
        method.setAccessible(true);

        method.invoke(activity, 7, false);

        TextView impact = activity.findViewById(R.id.txt_impact_value);
        assertTrue(impact.getText().toString().contains("kg CO2e / an"));
    }
}

