package com.uphf.sae6_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Écran de résultat du quiz de test.
 * Affiche le score /10 et le niveau (débutant/intermédiaire/difficile).
 */
public class QuizLevelResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level_result);

        TextView scoreTv = findViewById(R.id.level_result_score);
        TextView levelTv = findViewById(R.id.level_result_level);
        TextView messageTv = findViewById(R.id.level_result_message);
        Button backBtn = findViewById(R.id.level_result_back_btn);

        SharedPreferences prefs = getSharedPreferences(QuizLevelActivity.PREFS_NAME, MODE_PRIVATE);
        int score10 = prefs.getInt(QuizLevelActivity.KEY_USER_SCORE_10, -1);
        String levelKey = prefs.getString(QuizLevelActivity.KEY_USER_LEVEL, null);

        String levelLabel = levelLabel(levelKey);

        if (scoreTv != null) scoreTv.setText("Score : " + Math.max(0, score10) + " / 10");
        if (levelTv != null) levelTv.setText("Niveau : " + levelLabel);

        if (messageTv != null) {
            if (score10 < 0 || levelKey == null) {
                messageTv.setText("Aucun résultat trouvé. Refaire le quiz de test.");
            } else {
                messageTv.setText("Ton profil est enregistré. Tu peux maintenant accéder aux autres écrans.");
            }
        }

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private String levelLabel(String levelKey) {
        if (QuizLevelActivity.LEVEL_BEGINNER.equals(levelKey)) return "Débutant";
        if (QuizLevelActivity.LEVEL_INTERMEDIATE.equals(levelKey)) return "Intermédiaire";
        if (QuizLevelActivity.LEVEL_ADVANCED.equals(levelKey)) return "Difficile";
        return "Non défini";
    }
}

