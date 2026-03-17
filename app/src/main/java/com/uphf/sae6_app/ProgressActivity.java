package com.uphf.sae6_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    // Seuils "prêt à passer au niveau supérieur"
    // - Quiz normal : moyenne >= 70%
    // - Quiz de test : moyenne >= 7/10
    private static final double THRESHOLD_QUIZ_AVG_PERCENT = 70.0;
    private static final double THRESHOLD_LEVEL_AVG_ON_10 = 7.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        TextView readyTv = findViewById(R.id.progress_ready);

        TextView quizAvgTv = findViewById(R.id.quiz_avg);
        TextView quizHistoryTv = findViewById(R.id.quiz_history);

        TextView levelAvgTv = findViewById(R.id.level_avg);
        TextView levelHistoryTv = findViewById(R.id.level_history);

        List<Integer> quizScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_QUIZ);
        List<Integer> levelScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_LEVEL);

        double quizAvg = ScoreStorage.average(quizScores);
        double levelAvg = ScoreStorage.average(levelScores);

        if (quizAvgTv != null) {
            quizAvgTv.setText(String.format("Moyenne (5 derniers max) : %.1f%%", quizScores.isEmpty() ? 0.0 : quizAvg));
        }
        if (quizHistoryTv != null) {
            quizHistoryTv.setText(ScoreStorage.formatList(quizScores, "%"));
        }

        if (levelAvgTv != null) {
            levelAvgTv.setText(String.format("Moyenne (5 derniers max) : %.1f/10", levelScores.isEmpty() ? 0.0 : levelAvg));
        }
        if (levelHistoryTv != null) {
            levelHistoryTv.setText(ScoreStorage.formatList(levelScores, "/10"));
        }

        boolean ready = !quizScores.isEmpty() && !levelScores.isEmpty()
                && quizAvg >= THRESHOLD_QUIZ_AVG_PERCENT
                && levelAvg >= THRESHOLD_LEVEL_AVG_ON_10;

        if (readyTv != null) {
            if (ready) {
                readyTv.setText("Prêt à passer au niveau supérieur");
            } else {
                readyTv.setText("Continue ! Fais quelques quiz pour améliorer ta moyenne.");
            }
        }
    }
}
