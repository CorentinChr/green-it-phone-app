package com.uphf.sae6_app.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import com.uphf.sae6_app.R;
import com.uphf.sae6_app.data.local.ScoreStorage;
import com.uphf.sae6_app.data.retrofit.RetrofitClient;
import com.uphf.sae6_app.data.retrofit.ApiService;
import com.uphf.sae6_app.data.retrofit.dto.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Locale;

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

        final TextView readyTv = findViewById(R.id.progress_ready);

        final TextView quizAvgTv = findViewById(R.id.quiz_avg);
        final TextView quizHistoryTv = findViewById(R.id.quiz_history);

        final TextView levelAvgTv = findViewById(R.id.level_avg);
        final TextView levelHistoryTv = findViewById(R.id.level_history);
        final TextView qhAvgTv = findViewById(R.id.qh_avg);
        final TextView qhHistoryTv = findViewById(R.id.qh_history);
        final TextView arAvgTv = findViewById(R.id.ar_avg);
        final TextView arHistoryTv = findViewById(R.id.ar_history);

        // Méthode utilitaire locale pour (re)charger l'UI à partir du stockage local
        final Runnable refreshUi = () -> {
            List<Integer> quizScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_QUIZ);
            List<Integer> levelScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_LEVEL);
            List<Integer> qhScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_QH);
            List<Integer> arScores = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_AR);

            double quizAvg = ScoreStorage.average(quizScores);
            double levelAvg = ScoreStorage.average(levelScores);
            double qhAvg = ScoreStorage.average(qhScores);
            double arAvg = ScoreStorage.average(arScores);

            if (quizAvgTv != null) {
                quizAvgTv.setText(String.format(Locale.getDefault(), "Moyenne (5 derniers max) : %.1f%%", quizScores.isEmpty() ? 0.0 : quizAvg));
            }
            if (quizHistoryTv != null) {
                quizHistoryTv.setText(ScoreStorage.formatList(quizScores, "%"));
            }

            if (levelAvgTv != null) {
                levelAvgTv.setText(String.format(Locale.getDefault(), "Moyenne (5 derniers max) : %.1f/10", levelScores.isEmpty() ? 0.0 : levelAvg));
            }
            if (levelHistoryTv != null) {
                levelHistoryTv.setText(ScoreStorage.formatList(levelScores, "/10"));
            }

            if (qhAvgTv != null) {
                qhAvgTv.setText(String.format(Locale.getDefault(), "Moyenne (5 derniers max) : %.1f/10", qhScores.isEmpty() ? 0.0 : qhAvg));
            }
            if (qhHistoryTv != null) {
                qhHistoryTv.setText(ScoreStorage.formatList(qhScores, "/10"));
            }

            if (arAvgTv != null) {
                arAvgTv.setText(String.format(Locale.getDefault(), "Moyenne (5 derniers max) : %.1f/10", arScores.isEmpty() ? 0.0 : arAvg));
            }
            if (arHistoryTv != null) {
                arHistoryTv.setText(ScoreStorage.formatList(arScores, "/10"));
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
        };

        // Rafraîchissement initial (valeurs locales)
        refreshUi.run();

        // Tenter de récupérer la version serveur et écraser le stockage local si fourni
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<UserResponse> call = api.getUser(QuizLevelActivity.TEST_USER_ID);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse u = response.body();
                    if (u.scoreHistory != null) ScoreStorage.setScores(ProgressActivity.this, ScoreStorage.KEY_HISTORY_LEVEL, u.scoreHistory);
                    if (u.qhScoreHistory != null) ScoreStorage.setScores(ProgressActivity.this, ScoreStorage.KEY_HISTORY_QH, u.qhScoreHistory);
                    if (u.arScoreHistory != null) ScoreStorage.setScores(ProgressActivity.this, ScoreStorage.KEY_HISTORY_AR, u.arScoreHistory);
                    // recharger l'UI avec les données serveur
                    runOnUiThread(refreshUi);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // silent fail: on garde les valeurs locales
            }
        });

        // UI actualisée via refreshUi Runnable et via le callback réseau si disponible
    }
}
