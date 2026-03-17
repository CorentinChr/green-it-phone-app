package com.uphf.sae6_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QuizLevelActivity
 * Quiz de placement exécuté au premier accès (ou premier lancement selon branchement).
 * Objectif : calculer un score /10, enregistrer un niveau (débutant/intermédiaire/difficile)
 * puis permettre à QuizActivity de filtrer les questions selon ce niveau.
 */
public class QuizLevelActivity extends AppCompatActivity {

    // SharedPreferences
    public static final String PREFS_NAME = "prefs_user";
    public static final String KEY_LEVEL_DONE = "level_done";
    public static final String KEY_USER_LEVEL = "user_level"; // "beginner" | "intermediate" | "advanced"
    public static final String KEY_USER_SCORE_10 = "user_score_10"; // int (0..10)

    public static final String LEVEL_BEGINNER = "beginner";
    public static final String LEVEL_INTERMEDIATE = "intermediate";
    public static final String LEVEL_ADVANCED = "advanced";

    private TextView title;
    private TextView progress;
    private TextView question;
    private Button[] answerButtons = new Button[4];
    private TextView info;
    private Button nextBtn;

    private final List<LevelQuestion> items = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private boolean answeredCurrent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);

        title = findViewById(R.id.level_title);
        progress = findViewById(R.id.level_progress);
        question = findViewById(R.id.level_question);
        answerButtons[0] = findViewById(R.id.level_answer_0);
        answerButtons[1] = findViewById(R.id.level_answer_1);
        answerButtons[2] = findViewById(R.id.level_answer_2);
        answerButtons[3] = findViewById(R.id.level_answer_3);
        info = findViewById(R.id.level_info);
        nextBtn = findViewById(R.id.level_next_btn);

        loadLevelQuestions();

        for (int i = 0; i < answerButtons.length; i++) {
            final int idx = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(idx));
        }

        nextBtn.setOnClickListener(v -> showNext());

        displayCurrent();
    }

    private void loadLevelQuestions() {
        // 10 questions simples, orientées Green IT (peu importe le thème).
        // Convention: correctIndex (0..3)
        items.clear();

        items.add(new LevelQuestion(
                "Quel geste réduit le plus le stockage inutile de données ?",
                Arrays.asList("Tout garder pour plus tard", "Supprimer / archiver régulièrement", "Dupliquer tous les fichiers", "Envoyer des pièces jointes lourdes"),
                1
        ));

        items.add(new LevelQuestion(
                "Lequel consomme le plus de données (en général) ?",
                Arrays.asList("Texte", "Image compressée", "Vidéo HD", "E-mail sans pièce jointe"),
                2
        ));

        items.add(new LevelQuestion(
                "Quel est l’intérêt de se désabonner de newsletters inutiles ?",
                Arrays.asList("Recevoir plus d’e-mails", "Réduire les e-mails et le stockage", "Augmenter la pub", "Aucun"),
                1
        ));

        items.add(new LevelQuestion(
                "Pourquoi éviter les pièces jointes très lourdes ?",
                Arrays.asList("Ça ralentit le wifi seulement", "Ça augmente stockage + transfert", "Ça vide la batterie du téléphone", "Ça casse le clavier"),
                1
        ));

        items.add(new LevelQuestion(
                "Quel choix est le plus sobre pour partager un document ?",
                Arrays.asList("Envoyer 10 fois le même fichier", "Partage via lien (si possible)", "Photo de l’écran en HD", "Vidéo explicative"),
                1
        ));

        items.add(new LevelQuestion(
                "Quel paramètre a souvent un impact direct sur l’empreinte du streaming ?",
                Arrays.asList("La taille de police", "La qualité vidéo", "Le volume sonore", "La langue"),
                1
        ));

        items.add(new LevelQuestion(
                "En termes d’écoconception, une app est souvent plus sobre si elle…",
                Arrays.asList("Charge tout au démarrage", "Limite les requêtes inutiles", "Utilise beaucoup d’animations", "Télécharge en boucle"),
                1
        ));

        items.add(new LevelQuestion(
                "Quel est un bon réflexe sur le cloud ?",
                Arrays.asList("Multiplier les sauvegardes identiques", "Supprimer les doublons", "Uploader des vidéos non triées", "Ne jamais trier"),
                1
        ));

        items.add(new LevelQuestion(
                "Quel format est généralement plus léger ?",
                Arrays.asList("PNG (photo)", "JPEG (photo)", "RAW", "BMP"),
                1
        ));

        items.add(new LevelQuestion(
                "Nettoyer ses e-mails aide surtout à…",
                Arrays.asList("Réduire le CO2 des trajets", "Réduire stockage + synchronisation", "Améliorer la 4G", "Augmenter la RAM"),
                1
        ));
    }

    private void displayCurrent() {
        if (items.isEmpty()) {
            // Edge case (ne devrait pas arriver)
            question.setText("Aucune question de niveau disponible.");
            for (Button b : answerButtons) b.setVisibility(View.GONE);
            nextBtn.setText("Retour");
            nextBtn.setOnClickListener(v -> finish());
            return;
        }

        LevelQuestion q = items.get(currentIndex);

        if (title != null) title.setText("Évaluer votre niveau");
        if (progress != null) progress.setText("Question " + (currentIndex + 1) + " / " + items.size());
        if (question != null) question.setText(q.question);

        for (int i = 0; i < answerButtons.length; i++) {
            Button b = answerButtons[i];
            if (i < q.answers.size()) {
                b.setVisibility(View.VISIBLE);
                b.setText(q.answers.get(i));
                b.setEnabled(true);
                b.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                b.setVisibility(View.GONE);
            }
        }

        answeredCurrent = false;
        info.setVisibility(View.GONE);
        info.setText("");

        nextBtn.setText(currentIndex < items.size() - 1 ? "Suivant" : "Terminer");
        nextBtn.setVisibility(View.VISIBLE);
    }

    private void onAnswerSelected(int selectedIndex) {
        if (answeredCurrent) return;
        answeredCurrent = true;

        LevelQuestion q = items.get(currentIndex);

        for (Button b : answerButtons) b.setEnabled(false);

        if (selectedIndex == q.correctIndex) {
            score += 1;
            info.setText("Bonne réponse");
            answerButtons[selectedIndex].setBackgroundColor(Color.parseColor("#C8E6C9"));
        } else {
            info.setText("Mauvaise réponse");
            answerButtons[selectedIndex].setBackgroundColor(Color.parseColor("#FFCDD2"));
            answerButtons[q.correctIndex].setBackgroundColor(Color.parseColor("#C8E6C9"));
        }
        info.setVisibility(View.VISIBLE);
    }

    private void showNext() {
        if (items.isEmpty()) return;

        if (currentIndex < items.size() - 1) {
            currentIndex++;
            displayCurrent();
        } else {
            finishAndSave();
        }
    }

    private void finishAndSave() {
        int scoreOn10 = Math.max(0, Math.min(10, score));
        String level;
        // Seuils simples
        if (scoreOn10 <= 3) {
            level = LEVEL_BEGINNER;
        } else if (scoreOn10 <= 7) {
            level = LEVEL_INTERMEDIATE;
        } else {
            level = LEVEL_ADVANCED;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_LEVEL_DONE, true)
                .putString(KEY_USER_LEVEL, level)
                .putInt(KEY_USER_SCORE_10, scoreOn10)
                .apply();

        // Historique (5 derniers scores)
        ScoreStorage.addScore(this, ScoreStorage.KEY_HISTORY_LEVEL, scoreOn10, 5);

        // Afficher l'écran de résultat, puis retour à l'accueil (déblocage des cartes)
        Intent i = new Intent(this, QuizLevelResultActivity.class);
        startActivity(i);
        finish();
    }

    private static class LevelQuestion {
        final String question;
        final List<String> answers;
        final int correctIndex;

        LevelQuestion(String question, List<String> answers, int correctIndex) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
        }
    }
}
