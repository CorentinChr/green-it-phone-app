package com.uphf.sae6_app;

import android.view.View;
import android.widget.FrameLayout;

import com.uphf.sae6_app.model.GreenItData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DataVizAdapterTest {

    @Test
    public void beginnerFabrication_hidesDetailedFields() {
        GreenItData item = new GreenItData("Smartphone", 70.0, 10.0, 5.0, 3.0, "ADEME");
        DataVizAdapter adapter = new DataVizAdapter(
                RuntimeEnvironment.getApplication(),
                Collections.singletonList(item),
                QuizLevelActivity.LEVEL_BEGINNER,
                "fabrication"
        );

        FrameLayout parent = new FrameLayout(RuntimeEnvironment.getApplication());
        DataVizAdapter.Holder holder = adapter.onCreateViewHolder(parent, 0);
        adapter.onBindViewHolder(holder, 0);

        assertEquals(View.VISIBLE, holder.tvCo2Mfg.getVisibility());
        assertEquals(View.GONE, holder.tvEnergyMfg.getVisibility());
        assertEquals(View.GONE, holder.tvSource.getVisibility());
    }

    @Test
    public void advancedUsage_showsUsageAndSource() {
        GreenItData item = new GreenItData("Laptop", 200.0, 40.0, 50.0, 25.0, "ADEME");
        DataVizAdapter adapter = new DataVizAdapter(
                RuntimeEnvironment.getApplication(),
                Collections.singletonList(item),
                QuizLevelActivity.LEVEL_ADVANCED,
                "usage"
        );

        FrameLayout parent = new FrameLayout(RuntimeEnvironment.getApplication());
        DataVizAdapter.Holder holder = adapter.onCreateViewHolder(parent, 0);
        adapter.onBindViewHolder(holder, 0);

        assertEquals(View.VISIBLE, holder.tvEnergyUse.getVisibility());
        assertEquals(View.VISIBLE, holder.tvCo2Use.getVisibility());
        assertEquals(View.VISIBLE, holder.tvSource.getVisibility());
        assertEquals(View.GONE, holder.tvCo2Mfg.getVisibility());
    }
}

