package com.uphf.sae6_app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.uphf.sae6_app.ui.activities.QuizLevelActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * ScoreStorage
 * Petit utilitaire pour stocker les derniers scores (max N) dans SharedPreferences.
 *
 * Format : JSON array de numbers (scores en % ou sur 10 selon la liste).
 */
public final class ScoreStorage {

    private ScoreStorage() {
    }

    public static final String KEY_HISTORY_QUIZ = "history_quiz"; // quiz normal (score en %)
    public static final String KEY_HISTORY_LEVEL = "history_level"; // quiz de test (score /10)
    // Nouveaux historiques : quiz habitudes numériques (qh) et quiz AR
    public static final String KEY_HISTORY_QH = "history_qh"; // quiz habitudes numériques (score /10)
    public static final String KEY_HISTORY_AR = "history_ar"; // quiz application AR (score /10)

    public static void addScore(Context context, String historyKey, int score, int maxItems) {
        if (context == null) return;
        if (maxItems <= 0) maxItems = 5;

        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        List<Integer> current = getScores(context, historyKey);
        current.add(0, score); // plus récent en premier

        while (current.size() > maxItems) {
            current.remove(current.size() - 1);
        }

        JSONArray arr = new JSONArray();
        for (Integer s : current) {
            arr.put(s != null ? s : 0);
        }

        prefs.edit().putString(historyKey, arr.toString()).apply();
    }

    public static List<Integer> getScores(Context context, String historyKey) {
        List<Integer> result = new ArrayList<>();
        if (context == null) return result;

        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String raw = prefs.getString(historyKey, null);
        if (raw == null || raw.trim().isEmpty()) return result;

        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.optInt(i, 0));
            }
        } catch (JSONException ignored) {
            // données corrompues -> on repart à zéro
        }

        return result;
    }

    public static double average(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        long sum = 0;
        for (Integer s : scores) sum += (s != null ? s : 0);
        return (double) sum / (double) scores.size();
    }

    public static String formatList(List<Integer> scores, String suffix) {
        if (scores == null || scores.isEmpty()) return "—";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            sb.append(i + 1).append(". ").append(scores.get(i));
            if (suffix != null) sb.append(suffix);
            if (i < scores.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    public static void setScores(Context context, String historyKey, List<Integer> scores) {
        if (context == null) return;
        if (scores == null) scores = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(QuizLevelActivity.PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        for (Integer s : scores) arr.put(s != null ? s : 0);
        prefs.edit().putString(historyKey, arr.toString()).apply();
    }
}

