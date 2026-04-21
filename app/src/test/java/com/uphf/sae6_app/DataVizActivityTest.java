package com.uphf.sae6_app;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DataVizActivityTest {

    @Test
    public void onCreate_setsRecyclerAndSourceNote() {
        DataVizActivity activity = Robolectric.buildActivity(DataVizActivity.class).setup().get();

        RecyclerView rv = activity.findViewById(R.id.rv_data);
        TextView sourceNote = activity.findViewById(R.id.tv_source_note);

        assertNotNull(rv.getAdapter());
        assertTrue(sourceNote.getText().toString().length() > 0);
    }
}

