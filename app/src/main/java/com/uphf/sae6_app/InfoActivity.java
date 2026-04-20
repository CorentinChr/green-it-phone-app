package com.uphf.sae6_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uphf.sae6_app.model.InfoItem;
import android.widget.Toast;
import com.uphf.sae6_app.retrofit.RetrofitClient;
import com.uphf.sae6_app.retrofit.GreenItApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private ImageView infoImage;
    private TextView infoTitle;
    private TextView infoContent;
    private TextView infoCounter;
    private Button btnNext;
    private LinearLayout quizAnswersLayout;

    private List<InfoItem> items = new ArrayList<>();
    private int currentIndex = 0;
    private int currentStepIndex = 0;
    private List<InfoItem.InfoStep> currentSteps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        infoImage = findViewById(R.id.info_image);
        infoTitle = findViewById(R.id.info_title);
        infoContent = findViewById(R.id.info_content);
        infoCounter = findViewById(R.id.info_counter);
        btnNext = findViewById(R.id.btn_next_info);
        quizAnswersLayout = findViewById(R.id.quiz_answers_layout);

        // Récupérer le niveau de l'utilisateur (1=débutant, 2=intermédiaire, 3=difficile)
        int userDifficulty = getDifficultyFromUserLevel();
        // Récupérer le thème choisi (si passé en paramètre)
        String selectedTheme = null;
        if (getIntent() != null && getIntent().hasExtra("theme")) {
            selectedTheme = getIntent().getStringExtra("theme");
        }

        // Charger les fiches depuis l'API (remplace les données d'exemple)
        loadInfosFromApi(selectedTheme, userDifficulty);

        btnNext.setOnClickListener(v -> {
            InfoItem it = items.get(currentIndex);
            if (it.steps != null && !it.steps.isEmpty()) {
                if (currentStepIndex < it.steps.size() - 1) {
                    currentStepIndex++;
                    displayCurrent();
                } else if (currentIndex < items.size() - 1) {
                    currentIndex++;
                    currentStepIndex = 0;
                    currentSteps = null;
                    displayCurrent();
                } else {
                    finish();
                }
            } else {
                onNext();
            }
        });
    }

    // Récupère la difficulté de l'utilisateur depuis les préférences (comme QuizActivity)
    private int getDifficultyFromUserLevel() {
        android.content.SharedPreferences prefs = getSharedPreferences("prefs_user", MODE_PRIVATE);
        boolean done = prefs.getBoolean("level_done", false);
        if (!done) return 1; // Par défaut débutant
        String level = prefs.getString("user_level", null);
        if ("beginner".equals(level)) return 1;
        if ("intermediate".equals(level)) return 2;
        if ("advanced".equals(level)) return 3;
        return 1;
    }

    private void loadSampleInfos() {
        items.clear();
        // Fiche avec étapes texte + image + quiz
        List<InfoItem.InfoStep> steps1 = new ArrayList<>();
        steps1.add(new InfoItem.InfoStep("Éteignez les appareils en veille pour économiser l'énergie.", "placeholder"));
        steps1.add(new InfoItem.InfoStep("Privilégiez les ampoules LED pour réduire la consommation.", null));
        steps1.add(new InfoItem.InfoStep(new InfoItem.Quiz("Quel appareil consomme le plus en veille ?", java.util.Arrays.asList("Téléviseur", "Ordinateur portable", "Box internet", "Lampe LED"), 2)));
        items.add(new InfoItem("Réduire sa consommation électrique", 1, "energie", steps1));

        // Fiche avec texte + quiz
        List<InfoItem.InfoStep> steps2 = new ArrayList<>();
        steps2.add(new InfoItem.InfoStep("Le verre est recyclable à l'infini sans perte de qualité.", "placeholder"));
        steps2.add(new InfoItem.InfoStep(new InfoItem.Quiz("Que faut-il faire avant de recycler une bouteille en verre ?", java.util.Arrays.asList("La casser", "La rincer", "La peindre", "La remplir"), 1)));
        items.add(new InfoItem("Recycler le verre", 1, "dechets", steps2));

        // Fiche avec plusieurs étapes texte
        List<InfoItem.InfoStep> steps3 = new ArrayList<>();
        steps3.add(new InfoItem.InfoStep("Supprimez régulièrement vos anciens e-mails pour réduire l'empreinte numérique.", null));
        steps3.add(new InfoItem.InfoStep("Videz la corbeille pour libérer de l'espace sur les serveurs.", null));
        items.add(new InfoItem("Nettoyage des e-mails", 2, "numerique", steps3));

        // Fiche simple (ancienne version)
        items.add(new InfoItem(
                "Favoriser les transports actifs",
                "La marche et le vélo sont des alternatives durables pour les trajets courts, réduisant émissions et pollution.",
                "placeholder",
                3,
                "mobilite"));
    }

    private void loadInfosFromApi(String selectedTheme, int userDifficulty) {
        GreenItApi api = RetrofitClient.getInstance().create(GreenItApi.class);
        api.getInfos().enqueue(new Callback<java.util.List<InfoItem>>() {
            @Override
            public void onResponse(Call<java.util.List<InfoItem>> call, Response<java.util.List<InfoItem>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(InfoActivity.this, "Erreur API infos", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                items.clear();
                items.addAll(response.body());

                // Filtrer côté client
                List<InfoItem> filtered = new ArrayList<>();
                for (InfoItem item : items) {
                    boolean diffOk = item.difficulty <= userDifficulty;
                    boolean themeOk = (selectedTheme == null) || (item.theme != null && item.theme.equalsIgnoreCase(selectedTheme));
                    if (diffOk && themeOk) filtered.add(item);
                }
                items = filtered;

                // Afficher première fiche
                if (!items.isEmpty()) {
                    currentIndex = 0;
                    displayCurrent();
                } else {
                    infoTitle.setText("Aucune fiche disponible.");
                    infoContent.setText("");
                    btnNext.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<java.util.List<InfoItem>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InfoActivity.this, "Erreur réseau infos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayCurrent() {
        InfoItem it = items.get(currentIndex);
        infoTitle.setText(it.title != null ? it.title : "");
        // Si la fiche a des étapes interactives
        if (it.steps != null && !it.steps.isEmpty()) {
            if (currentSteps != it.steps) {
                currentSteps = it.steps;
                currentStepIndex = 0;
            }
            displayStep(it.steps.get(currentStepIndex));
            infoCounter.setText(String.format("%d / %d (étape %d/%d)", currentIndex + 1, items.size(), currentStepIndex + 1, it.steps.size()));
            btnNext.setText(currentStepIndex < it.steps.size() - 1 ? "Voir la suite" : (currentIndex < items.size() - 1 ? "Fiche suivante" : "Terminer"));
        } else {
            // Fiche simple (ancienne version)
            infoContent.setVisibility(View.VISIBLE);
            infoContent.setText(it.content != null ? it.content : "");
            infoImage.setVisibility(it.imageName != null && !it.imageName.isEmpty() ? View.VISIBLE : View.GONE);
            if (it.imageName != null && !it.imageName.isEmpty()) {
                int resId = getResources().getIdentifier(it.imageName, "drawable", getPackageName());
                if (resId != 0) infoImage.setImageResource(resId);
            }
            infoCounter.setText(String.format("%d / %d", currentIndex + 1, items.size()));
            btnNext.setText(currentIndex < items.size() - 1 ? "Suivant" : "Terminer");
        }
    }

    private void displayStep(InfoItem.InfoStep step) {
        // s'assurer que le champ quiz est initialisé même si l'API renvoie
        // les propriétés du quiz à plat (question, answers, correctIndex)
        if (step != null) step.ensureQuiz();
        if (quizAnswersLayout != null) quizAnswersLayout.removeAllViews();
        if (step.quiz != null) {
            infoContent.setVisibility(View.VISIBLE);
            infoContent.setText(step.quiz.question);
            infoImage.setVisibility(View.GONE);
            if (quizAnswersLayout != null) {
                for (int i = 0; i < step.quiz.answers.size(); i++) {
                    Button answerBtn = new Button(this);
                    answerBtn.setText(step.quiz.answers.get(i));
                    int idx = i;
                    answerBtn.setOnClickListener(v -> handleQuizAnswer(step, idx, answerBtn));
                    quizAnswersLayout.addView(answerBtn);
                }
                quizAnswersLayout.setVisibility(View.VISIBLE);
            }
            btnNext.setEnabled(false);
        } else {
            infoContent.setVisibility(View.VISIBLE);
            infoContent.setText(step.text != null ? step.text : "");
            if (step.imageName != null && !step.imageName.isEmpty()) {
                int resId = getResources().getIdentifier(step.imageName, "drawable", getPackageName());
                if (resId != 0) {
                    infoImage.setImageResource(resId);
                    infoImage.setVisibility(View.VISIBLE);
                } else {
                    infoImage.setVisibility(View.GONE);
                }
            } else {
                infoImage.setVisibility(View.GONE);
            }
            if (quizAnswersLayout != null) quizAnswersLayout.setVisibility(View.GONE);
            btnNext.setEnabled(true);
        }
    }

    private void handleQuizAnswer(InfoItem.InfoStep step, int selectedIdx, Button answerBtn) {
        boolean correct = selectedIdx == step.quiz.correctIndex;
        answerBtn.setBackgroundColor(getResources().getColor(correct ? android.R.color.holo_green_light : android.R.color.holo_red_light));
        for (int i = 0; i < quizAnswersLayout.getChildCount(); i++) {
            quizAnswersLayout.getChildAt(i).setEnabled(false);
        }
        btnNext.setEnabled(true);
        infoContent.setText(step.quiz.question + "\n\n" + (correct ? "Bonne réponse !" : "Mauvaise réponse. La bonne réponse était : " + step.quiz.answers.get(step.quiz.correctIndex)));
    }

    private void onNext() {
        if (currentIndex < items.size() - 1) {
            currentIndex++;
            displayCurrent();
        } else {
            // Fin: fermer l'activité
            finish();
        }
    }
}
