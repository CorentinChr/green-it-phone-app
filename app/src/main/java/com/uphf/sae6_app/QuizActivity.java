package com.uphf.sae6_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        // Charger des exemples (dans une vraie app on chargerait depuis un backend)
        loadSampleQuestions();

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

        applyFilters(themeFilter, difficultyFilter);

        // Set listeners
        for (int i = 0; i < answerButtons.length; i++) {
            final int idx = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(idx));
        }

        prevBtn.setOnClickListener(v -> showPrevious());
        nextBtn.setOnClickListener(v -> showNext());

        // Afficher la première question ou message si aucune question
        if (items.isEmpty()) {
            quizQuestion.setText("Aucune question disponible pour ce filtre.");
            for (Button b : answerButtons) b.setVisibility(View.GONE);
            quizImage.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
            nextBtn.setText("Retour");
            nextBtn.setOnClickListener(v -> finish());
        } else {
            currentIndex = 0;
            displayCurrent();
        }
    }

    private void loadSampleQuestions() {
        // Exemple de données (images supposées présentes dans res/drawable si on veut les afficher)
        allItems.add(new QuizItem(1,
                "Quel est le principal gaz responsable de l'effet de serre ?",
                Arrays.asList("Oxygène", "Dioxyde de carbone", "Azote", "Hélium"),
                1,
                "Bonne réponse ! Le CO2 est l'un des gaz à effet de serre les plus importants.",
                "climat",
                1,
                "placeholder")); // Image placeholder

        allItems.add(new QuizItem(2,
                "Quelle action réduit la consommation d'énergie domestique ?",
                Arrays.asList("Laisser les appareils en veille", "Installer des ampoules LED", "Ouvrir les fenêtres en hiver", "Utiliser de l'eau chaude souvent"),
                1,
                "Les ampoules LED consomment beaucoup moins d'énergie que les ampoules incandescentes.",
                "energie",
                1,
                "ic_quiz_energy")); // L'image n'existe pas donc l'app n'affiche rien

        allItems.add(new QuizItem(3,
                "Quel matériau est le plus facilement recyclable ?",
                Arrays.asList("Plastique mixte", "Verre", "Textile mélangé", "Contenant composite"),
                1,
                "Le verre est recyclable indéfiniment sans perte de qualité.",
                "dechets",
                2,
                "ic_quiz_glass"));

        allItems.add(new QuizItem(4,
                "Quelle est la durée de décomposition approximative d'une bouteille en plastique ?",
                Arrays.asList("10 ans", "100 ans", "450 ans", "1 an"),
                2,
                "Une bouteille plastique peut mettre plusieurs centaines d'années à se décomposer.",
                "dechets",
                3,
                null)); // Pas d'image pour cette question
    }

    private void applyFilters(String theme, int difficulty) {
        items.clear();
        for (QuizItem q : allItems) {
            boolean themeOk = (theme == null) || (q.theme != null && q.theme.equalsIgnoreCase(theme));
            boolean diffOk = (difficulty <= 0) || (q.difficulty == difficulty);
            if (themeOk && diffOk) items.add(q);
        }
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

        // Info
        quizInfo.setVisibility(View.GONE);
        quizInfo.setText("");

        // Prev/Next
        prevBtn.setVisibility(currentIndex > 0 ? View.VISIBLE : View.GONE);
        nextBtn.setText(currentIndex < items.size() - 1 ? "Suivant" : "Terminer");
    }

    private void onAnswerSelected(int selectedIndex) {
        QuizItem q = items.get(currentIndex);

        // Désactiver boutons
        for (Button b : answerButtons) b.setEnabled(false);

        if (selectedIndex == q.correctIndex) {
            // Bonne réponse
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
