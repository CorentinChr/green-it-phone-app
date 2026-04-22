
package com.uphf.sae6_app.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.uphf.sae6_app.R;
import com.uphf.sae6_app.data.local.ScoreStorage;
import com.uphf.sae6_app.data.retrofit.RetrofitClient;
import com.uphf.sae6_app.data.retrofit.ApiService;
import com.uphf.sae6_app.data.retrofit.dto.UserRequest;
import com.uphf.sae6_app.data.retrofit.dto.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "prefs_user";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_LEVEL = "user_level";

    private TextView textViewName;
    private EditText editTextName;
    private Button buttonChangeName;
    private Button buttonSave;
    private Spinner spinnerLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewName = findViewById(R.id.textViewName);
        editTextName = findViewById(R.id.editTextName);
        buttonChangeName = findViewById(R.id.buttonChangeName);
        buttonSave = findViewById(R.id.buttonSave);
        spinnerLevel = findViewById(R.id.spinnerLevel);

        // Préparer le Spinner des niveaux
        String[] niveaux = {"Débutant", "Intermédiaire", "Avancé"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, niveaux);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);

        // Charger les valeurs existantes
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userName = prefs.getString(KEY_USER_NAME, "");
        String userLevel = prefs.getString(KEY_USER_LEVEL, "Débutant");
        textViewName.setText(userName.isEmpty() ? "-" : userName);
        editTextName.setText(userName);
        // Sélectionner le niveau dans le Spinner
        int niveauIndex = 0;
        for (int i = 0; i < niveaux.length; i++) {
            if (niveaux[i].equalsIgnoreCase(userLevel)) {
                niveauIndex = i;
                break;
            }
        }
        spinnerLevel.setSelection(niveauIndex);

        // Affichage/édition du nom (on montre simplement le champ pour modification)
        buttonChangeName.setOnClickListener(v -> {
            textViewName.setVisibility(TextView.GONE);
            buttonChangeName.setVisibility(Button.GONE);
            editTextName.setVisibility(EditText.VISIBLE);
        });

        // Bouton Enregistrer : sauvegarde nom + niveau et revient à l'accueil
        buttonSave.setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
                return;
            }

            // Map affichage -> clefs stockées
            String selectedLabel = (String) spinnerLevel.getSelectedItem();
            String levelKey = QuizLevelActivity.LEVEL_BEGINNER; // default
            if ("Débutant".equalsIgnoreCase(selectedLabel)) levelKey = QuizLevelActivity.LEVEL_BEGINNER;
            else if ("Intermédiaire".equalsIgnoreCase(selectedLabel) || "Intermediaire".equalsIgnoreCase(selectedLabel)) levelKey = QuizLevelActivity.LEVEL_INTERMEDIATE;
            else if ("Avancé".equalsIgnoreCase(selectedLabel) || "Avance".equalsIgnoreCase(selectedLabel) || "Avancé".equals(selectedLabel)) levelKey = QuizLevelActivity.LEVEL_ADVANCED;

            prefs.edit()
                    .putString(KEY_USER_NAME, newName)
                    .putString(KEY_USER_LEVEL, levelKey)
                    .apply();

            // Désactiver le bouton pendant l'upload
            buttonSave.setEnabled(false);

            // Lancer l'envoi vers le backend
            uploadProfileToBackend(newName, levelKey, () -> {
                // success
                buttonSave.setEnabled(true);
                Toast.makeText(this, "Profil enregistré et mis à jour sur le serveur", Toast.LENGTH_SHORT).show();
                finish();
            }, (errMsg) -> {
                // error
                buttonSave.setEnabled(true);
                Toast.makeText(this, "Envoi échoué: " + errMsg, Toast.LENGTH_LONG).show();
            });
        });

        // Pas de sauvegarde immédiate du niveau : sera fait au clic sur Enregistrer
    }

    // Simple callback interface pour erreur
    private interface ErrCallback { void onError(String msg); }

    private void uploadProfileToBackend(String name, String levelKey, final Runnable onComplete, final ErrCallback onError) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(QuizLevelActivity.KEY_USER_ID, QuizLevelActivity.TEST_USER_ID);

        int scoreOn10 = prefs.getInt(QuizLevelActivity.KEY_USER_SCORE_10, 0);
        List<Integer> history = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_LEVEL);
        if (history.isEmpty()) {
            history = Collections.singletonList(scoreOn10);
        }

        // Récupérer également les historiques additionnels (habitudes numériques et AR)
        List<Integer> qhHistory = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_QH);
        List<Integer> arHistory = ScoreStorage.getScores(this, ScoreStorage.KEY_HISTORY_AR);
        if (qhHistory.isEmpty()) qhHistory = Collections.emptyList();
        if (arHistory.isEmpty()) arHistory = Collections.emptyList();

        UserRequest req = new UserRequest(userId, name, levelKey, scoreOn10, history, qhHistory, arHistory);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<UserResponse> call = api.upsertUser(req);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("ProfileActivity", "User uploaded: " + userId);
                    if (onComplete != null) onComplete.run();
                } else {
                    String msg = "code " + response.code();
                    Log.w("ProfileActivity", "Upload failed: " + msg);
                    if (onError != null) onError.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("ProfileActivity", "Upload error", t);
                if (onError != null) onError.onError(t.getMessage() != null ? t.getMessage() : "Erreur réseau");
            }
        });
    }
}
