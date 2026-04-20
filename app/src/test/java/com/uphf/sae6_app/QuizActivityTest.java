package com.uphf.sae6_app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class QuizActivityTest {

    @Test
    public void normalizeKey_removesAccentsAndSymbols() throws Exception {
        Method method = QuizActivity.class.getDeclaredMethod("normalizeKey", String.class);
        method.setAccessible(true);

        String normalized = (String) method.invoke(null, "Énergie durable !");
        assertEquals("energiedurable", normalized);
    }
}

