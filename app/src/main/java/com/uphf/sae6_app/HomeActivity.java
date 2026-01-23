package com.uphf.sae6_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Profil (données fictives) ---
        TextView name = findViewById(R.id.user_name);
        TextView score = findViewById(R.id.user_score);
        ImageView avatar = findViewById(R.id.avatar);

        if (name != null) name.setText(getString(R.string.user_name_default));
        if (score != null) score.setText(getString(R.string.user_score_default));
        if (avatar != null) avatar.setContentDescription(getString(R.string.avatar_description));

        // --- Cartes / navigation ---
        // Récupération des includes (les layouts inclus retournent un View root)
        View cardDashboard = findViewById(R.id.card_dashboard);
        View cardQuiz = findViewById(R.id.card_quiz);
        View cardInfo = findViewById(R.id.card_info);
        View cardProgress = findViewById(R.id.card_progress);
        View cardProfile = findViewById(R.id.card_profile);

        // Wiring navigation (intents vers activities placeholders)
        if (cardDashboard != null) cardDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        if (cardQuiz != null) cardQuiz.setOnClickListener(v -> startActivity(new Intent(this, QuizActivity.class)));
        if (cardInfo != null) cardInfo.setOnClickListener(v -> startActivity(new Intent(this, InfoActivity.class)));
        if (cardProgress != null) cardProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
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
        if (cardProfile != null) {
            t = cardProfile.findViewById(R.id.card_title);
            if (t != null) t.setText(getString(R.string.card_profile));
        }
    }
}
