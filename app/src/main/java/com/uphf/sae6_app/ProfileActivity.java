
package com.uphf.sae6_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

            Toast.makeText(this, "Profil enregistré", Toast.LENGTH_SHORT).show();
            // Retour vers l'activité précédente (Accueil) — HomeActivity fera le refresh en onResume
            finish();
        });

        // Pas de sauvegarde immédiate du niveau : sera fait au clic sur Enregistrer
    }
}
