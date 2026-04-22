package com.uphf.sae6_app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uphf.sae6_app.R;
import com.uphf.sae6_app.data.local.ScoreStorage;
import com.uphf.sae6_app.model.QuizItem;
import android.widget.Toast;
import com.uphf.sae6_app.data.retrofit.RetrofitClient;
import com.uphf.sae6_app.data.retrofit.GreenItApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ImageView quizImage;
    private TextView quizQuestion;
    private Button[] answerButtons = new Button[4];
    private TextView quizInfo;
    private Button prevBtn;
    private Button nextBtn;

    private List<QuizItem> allItems = new ArrayList<>();
    private List<QuizItem> items = new ArrayList<>(); // items filtrés
    private int currentIndex = 0;

    // Scoring
    private int scoreCorrect = 0;
    private boolean answeredCurrent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizImage = findViewById(R.id.quiz_image);
        quizQuestion = findViewById(R.id.quiz_question);
        answerButtons[0] = findViewById(R.id.answer_0);
        answerButtons[1] = findViewById(R.id.answer_1);
        answerButtons[2] = findViewById(R.id.answer_2);
        answerButtons[3] = findViewById(R.id.answer_3);
        quizInfo = findViewById(R.id.quiz_info);
        prevBtn = findViewById(R.id.prev_btn);
        nextBtn = findViewById(R.id.next_btn);

        // Charger les questions depuis l'API (remplace les données d'exemple)

        // Lire les paramètres de filtre (theme et difficulty) s'ils sont passés via Intent
        Intent intent = getIntent();
        String themeFilter = null;
        int difficultyFilter = -1; // -1 = aucun filtre
        if (intent != null) {
            if (intent.hasExtra("theme")) {
                themeFilter = intent.getStringExtra("theme");
            }
            if (intent.hasExtra("difficulty")) {
                difficultyFilter = intent.getIntExtra("difficulty", -1);
            }
        }

        // Log du filtre reçu pour debug
        android.util.Log.d("QuizActivity", "themeFilter reçu (onCreate): " + themeFilter + " difficultyFilter (initial): " + difficultyFilter);

        // Si aucune difficulté n'est fournie, on applique le niveau enregistré (si existant)
        if (difficultyFilter <= 0) {
            difficultyFilter = getDifficultyFromUserLevel();
        }

        // Set listeners
        for (int i = 0; i < answerButtons.length; i++) {
            final int idx = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(idx));
        }

        prevBtn.setOnClickListener(v -> showPrevious());
        nextBtn.setOnClickListener(v -> showNext());
        // Lancer le chargement depuis l'API (le callback gérera l'affichage)
        loadQuizFromApi(themeFilter, difficultyFilter);
    }

    private int getDifficultyFromUserLevel() {
        SharedPreferences prefs = getSharedPreferences(QuizLevelActivity.PREFS_NAME, MODE_PRIVATE);
        boolean done = prefs.getBoolean(QuizLevelActivity.KEY_LEVEL_DONE, false);
        if (!done) return -1;

        String level = prefs.getString(QuizLevelActivity.KEY_USER_LEVEL, null);
        if (QuizLevelActivity.LEVEL_BEGINNER.equals(level)) return 1;
        if (QuizLevelActivity.LEVEL_INTERMEDIATE.equals(level)) return 2;
        if (QuizLevelActivity.LEVEL_ADVANCED.equals(level)) return 3;
        return -1;
    }

    private void loadQuizFromApi(String themeFilter, int difficultyFilter) {
        GreenItApi api = RetrofitClient.getInstance().create(GreenItApi.class);
        api.getQuiz().enqueue(new Callback<java.util.List<QuizItem>>() {
            @Override
            public void onResponse(Call<java.util.List<QuizItem>> call, Response<java.util.List<QuizItem>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(QuizActivity.this, "Erreur API quiz", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                allItems.clear();
                allItems.addAll(response.body());
                // après avoir rempli allItems dans onResponse
                for (QuizItem qi : allItems) {
                    android.util.Log.d("QuizActivity", "quiz id=" + qi.id + " theme=" + qi.theme);
                }
                android.util.Log.d("QuizActivity", "items totaux: " + allItems.size());

                // Appliquer les filtres côté client
                applyFilters(themeFilter, difficultyFilter);

                if (items.isEmpty()) {
                    quizQuestion.setText("Aucune question disponible pour ce filtre.");
                    for (Button b : answerButtons) b.setVisibility(View.GONE);
                    quizImage.setVisibility(View.GONE);
                    prevBtn.setVisibility(View.GONE);
                    nextBtn.setText("Retour");
                    nextBtn.setOnClickListener(v -> finish());
                } else {
                    currentIndex = 0;
                    scoreCorrect = 0;
                    answeredCurrent = false;
                    displayCurrent();
                }
            }

            @Override
            public void onFailure(Call<java.util.List<QuizItem>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(QuizActivity.this, "Erreur réseau quiz: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void applyFilters(String theme, int difficulty) {
        items.clear();

        String normalizedFilter = normalizeKey(theme);
        android.util.Log.d("QuizActivity", "applyFilters: normalizedFilter=" + normalizedFilter + " difficulty=" + difficulty + " allItems=" + allItems.size());

        for (QuizItem q : allItems) {
            String qThemeNorm = normalizeKey(q.theme);

            boolean themeOk;
            if (normalizedFilter == null) {
                themeOk = true;
            } else if (qThemeNorm != null && qThemeNorm.equals(normalizedFilter)) {
                themeOk = true;
            } else {
                // fallback permissif: accepter si l'un contient l'autre (après normalisation)
                themeOk = (qThemeNorm != null && (qThemeNorm.contains(normalizedFilter) || normalizedFilter.contains(qThemeNorm)));
            }

            // Accepter les questions dont la difficulté est inférieure ou égale
            // au niveau de l'utilisateur (comme dans InfoActivity)
            boolean diffOk = (difficulty <= 0) || (q.difficulty <= difficulty);
            if (themeOk && diffOk) {
                items.add(q);
                android.util.Log.d("QuizActivity", "kept quiz id=" + q.id + " theme=" + q.theme + " normalized=" + qThemeNorm);
            }
        }

        android.util.Log.d("QuizActivity", "items après filtrage: " + items.size());
    }

    private static String normalizeKey(String s) {
        if (s == null) return null;
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}", ""); // supprime accents
        n = n.replaceAll("[^A-Za-z0-9]", ""); // supprime espaces/symboles
        return n.toLowerCase();
    }

    private void displayCurrent() {
        QuizItem q = items.get(currentIndex);
        quizQuestion.setText(q.question);

        // Image
        if (q.image != null && !q.image.isEmpty()) {
            int resId = getResources().getIdentifier(q.image, "drawable", getPackageName());
            if (resId != 0) {
                quizImage.setImageResource(resId);
                quizImage.setVisibility(View.VISIBLE);
            } else {
                quizImage.setVisibility(View.GONE);
            }
        } else {
            quizImage.setVisibility(View.GONE);
        }

        // Réponses
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < q.answers.size()) {
                answerButtons[i].setText(q.answers.get(i));
                answerButtons[i].setVisibility(View.VISIBLE);
                answerButtons[i].setEnabled(true);
                answerButtons[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                answerButtons[i].setVisibility(View.GONE);
            }
        }

        answeredCurrent = false;

        // Info
        quizInfo.setVisibility(View.GONE);
        quizInfo.setText("");

        // Prev/Next
        prevBtn.setVisibility(currentIndex > 0 ? View.VISIBLE : View.GONE);
        nextBtn.setText(currentIndex < items.size() - 1 ? "Suivant" : "Terminer");
    }

    private void onAnswerSelected(int selectedIndex) {
        if (answeredCurrent) return;
        answeredCurrent = true;

        QuizItem q = items.get(currentIndex);

        // Désactiver boutons
        for (Button b : answerButtons) b.setEnabled(false);

        if (selectedIndex == q.correctIndex) {
            // Bonne réponse
            scoreCorrect += 1;
            quizInfo.setText(q.infos != null ? q.infos : "Bonne réponse !");
            quizInfo.setVisibility(View.VISIBLE);
            // Marquer bouton en vert
            answerButtons[selectedIndex].setBackgroundColor(Color.parseColor("#C8E6C9"));
        } else {
            // Mauvaise réponse
            quizInfo.setText("Mauvaise réponse. Réponse correcte : " + q.answers.get(q.correctIndex));
            quizInfo.setVisibility(View.VISIBLE);
            // Marquer sélection et correcte
            answerButtons[selectedIndex].setBackgroundColor(Color.parseColor("#FFCDD2"));
            answerButtons[q.correctIndex].setBackgroundColor(Color.parseColor("#C8E6C9"));
        }

        // Montrer bouton suivant
        nextBtn.setVisibility(View.VISIBLE);
    }

    private void showNext() {
        if (items.isEmpty()) return;
        if (currentIndex < items.size() - 1) {
            currentIndex++;
            displayCurrent();
        } else {
            // Fin du quiz
            int total = Math.max(1, items.size());
            int percent = (int) Math.round((scoreCorrect * 100.0) / total);
            ScoreStorage.addScore(this, ScoreStorage.KEY_HISTORY_QUIZ, percent, 5);
            finish();
        }
    }

    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrent();
        }
    }
}
