package com.uphf.sae6_app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uphf.sae6_app.R;
import com.uphf.sae6_app.ui.fragments.UsernameDialogFragment;
import com.uphf.sae6_app.data.local.UserPrefs;

/**
 * HomeActivity
 * Page d'accueil (UI) pour l'application Green IT.
 * - Affiche un header (Toolbar)
 * - Affiche un profil utilisateur fictif (nom + score)
 * - Propose plusieurs cartes menant aux écrans: Dashboard, Quiz, Fiches, Progress, Profil
 *
 * Note: Aucun logique métier n'est implémentée ici, seulement la navigation vers des activities
 * placeholders. Le code est volontairement simple et commenté pour faciliter l'évolution.
 */
public class HomeActivity extends AppCompatActivity {

    private View homeCardsGrid;
    private TextView quizTestHint;

    private TextView scoreView;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Profil (données fictives) ---
        nameView = findViewById(R.id.user_name);
        scoreView = findViewById(R.id.user_score);
        ImageView avatar = findViewById(R.id.avatar);
        // name will be initialisé dans refreshLockedUi()
        if (scoreView != null) scoreView.setText(getString(R.string.user_score_default));
        if (avatar != null) avatar.setContentDescription(getString(R.string.avatar_description));

        // Bouton quiz de test
        Button btnQuizTest = findViewById(R.id.btn_quiz_test);
        quizTestHint = findViewById(R.id.quiz_test_hint);
        homeCardsGrid = findViewById(R.id.home_cards_grid);

        if (btnQuizTest != null) {
            btnQuizTest.setOnClickListener(v -> startActivity(new Intent(this, QuizLevelActivity.class)));
        }

        // --- Cartes / navigation ---
        // Récupération des includes (les layouts inclus retournent un View root)
        View cardDashboard = findViewById(R.id.card_dashboard);
        View cardQuiz = findViewById(R.id.card_quiz);
        View cardInfo = findViewById(R.id.card_info);
        View cardProgress = findViewById(R.id.card_progress);
        View cardDataViz = findViewById(R.id.card_data_viz);
        View cardProfile = findViewById(R.id.card_profile);

        // Wiring navigation (intents)
        if (cardDashboard != null) cardDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        if (cardQuiz != null) cardQuiz.setOnClickListener(v -> showThemeDialogAndStartQuiz());
        if (cardInfo != null) cardInfo.setOnClickListener(v -> showThemeDialogAndStartInfo());
        if (cardProgress != null) cardProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        if (cardDataViz != null) cardDataViz.setOnClickListener(v -> startActivity(new Intent(this, DataVizActivity.class)));
        if (cardProfile != null) cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // --- Mise à jour des titres des cartes (réutilise le layout item_card_home) ---
        TextView t;
        if (cardDashboard != null) {
            t = cardDashboard.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_dashboard));
        }
        if (cardQuiz != null) {
            t = cardQuiz.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_quiz));
        }
        if (cardInfo != null) {
            t = cardInfo.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_info));
        }
        if (cardProgress != null) {
            t = cardProgress.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_progress));
        }
        if (cardDataViz != null) {
            t = cardDataViz.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_data_viz));
        }
        if (cardProfile != null) {
            t = cardProfile.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_profile));
        }

        // Si aucun nom d'utilisateur n'est enregistré, demander le prénom dès le premier lancement
        if (!UserPrefs.hasUserName(this)) {
            UsernameDialogFragment dlg = new UsernameDialogFragment();
            dlg.setCancelable(false);
            dlg.show(getSupportFragmentManager(), "username_dialog");
        }

        refreshLockedUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLockedUi();
    }

    private void refreshLockedUi() {
        SharedPreferences prefs = getSharedPreferences(QuizLevelActivity.PREFS_NAME, MODE_PRIVATE);
        // Mettre à jour le nom affiché dans le header
        if (nameView != null) {
            String storedName = prefs.getString("user_name", null);
            if (storedName == null || storedName.trim().isEmpty()) {
                nameView.setText(getString(R.string.user_name_default));
            } else {
                nameView.setText(storedName);
            }
        }
        boolean hasScore = prefs.getBoolean(QuizLevelActivity.KEY_LEVEL_DONE, false)
                && prefs.contains(QuizLevelActivity.KEY_USER_LEVEL)
                && prefs.contains(QuizLevelActivity.KEY_USER_SCORE_10);

        if (homeCardsGrid != null) {
            homeCardsGrid.setVisibility(hasScore ? View.VISIBLE : View.GONE);
        }
        if (quizTestHint != null) {
            quizTestHint.setVisibility(hasScore ? View.GONE : View.VISIBLE);
        }

        // Mettre à jour la zone "score" pour afficher le profil (score + niveau)
        if (scoreView != null) {
            if (!hasScore) {
                scoreView.setText(getString(R.string.user_score_default));
            } else {
                int score10 = prefs.getInt(QuizLevelActivity.KEY_USER_SCORE_10, 0);
                String levelKey = prefs.getString(QuizLevelActivity.KEY_USER_LEVEL, "");
                String levelLabel;
                if (QuizLevelActivity.LEVEL_BEGINNER.equals(levelKey)) levelLabel = "Débutant";
                else if (QuizLevelActivity.LEVEL_INTERMEDIATE.equals(levelKey)) levelLabel = "Intermédiaire";
                else if (QuizLevelActivity.LEVEL_ADVANCED.equals(levelKey)) levelLabel = "Difficile";
                else levelLabel = "Non défini";

                scoreView.setText("Quiz de test : " + score10 + "/10 • Niveau : " + levelLabel);
            }
        }
    }

    // Affiche un dialog pour choisir le thème et lance QuizActivity avec le thème choisi
    private void showThemeDialogAndStartQuiz() {
        final String[] themes = {"energie", "dechets", "numerique", "mobilite"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choisissez un thème")
                .setItems(themes, (dialog, which) -> {
                    Intent intent = new Intent(this, QuizActivity.class);
                    intent.putExtra("theme", themes[which]);
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // Affiche un dialog pour choisir le thème et lance InfoActivity avec le thème choisi
    private void showThemeDialogAndStartInfo() {
        final String[] themes = {"energie", "dechets", "numerique", "mobilite"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choisissez un thème")
                .setItems(themes, (dialog, which) -> {
                    Intent intent = new Intent(this, InfoActivity.class);
                    intent.putExtra("theme", themes[which]);
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
