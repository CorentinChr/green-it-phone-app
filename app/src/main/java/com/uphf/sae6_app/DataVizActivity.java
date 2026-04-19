package com.uphf.sae6_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uphf.sae6_app.model.GreenItData;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity de visualisation des données Green IT.
 * Cette version met en place un affichage par liste (RecyclerView) avec différents niveaux de détail
 * selon le niveau de l'utilisateur (stocké dans SharedPreferences par le QuizLevelActivity).
 */
public class DataVizActivity extends AppCompatActivity {

    private RecyclerView rvData;
    private TextView tvSourceNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viz);

        rvData = findViewById(R.id.rv_data);
        tvSourceNote = findViewById(R.id.tv_source_note);

        // Récupérer le niveau utilisateur depuis les prefs (quand le quiz a été fait)
        SharedPreferences prefs = getSharedPreferences(QuizLevelActivity.PREFS_NAME, MODE_PRIVATE);
        String userLevel = prefs.getString(QuizLevelActivity.KEY_USER_LEVEL, QuizLevelActivity.LEVEL_BEGINNER);

        // Spinner pour choisir la categorie a afficher
        android.widget.Spinner spinner = findViewById(R.id.spinner_category);
        TextView tvCatDesc = findViewById(R.id.tv_category_description);

        // Charger des données d'exemple (simulées / indicatives — ADEME)
        List<GreenItData> items = loadSampleData();

        // Setup initial category
        String initialCategory = getResources().getStringArray(R.array.data_categories_values)[0];

        DataVizAdapter adapter = new DataVizAdapter(this, items, userLevel, initialCategory);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(adapter);

        // Mettre a jour description et adapter quand spinner change
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String cat = getResources().getStringArray(R.array.data_categories_values)[position];
                // update description
                if ("fabrication".equalsIgnoreCase(cat)) {
                    tvCatDesc.setText(getString(R.string.desc_manufacturing));
                } else {
                    tvCatDesc.setText(getString(R.string.desc_usage));
                }
                // recreate adapter with new category
                DataVizAdapter newAdapter = new DataVizAdapter(DataVizActivity.this, items, userLevel, cat);
                rvData.setAdapter(newAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Footer / note sur la source (déjà en string ressources)
        tvSourceNote.setText(getString(R.string.data_source_note));
    }

    private List<GreenItData> loadSampleData() {
        List<GreenItData> list = new ArrayList<>();
        String src = "ADEME (exemple)";

        // Valeurs indicatives et simplifiées :
        // Parameters: device, co2ManufacturingKg, energyManufacturingKwh, energyUseKwhPerYear, co2UseKgPerYear, source
        list.add(new GreenItData("Smartphone", 70.0, 10.0, 5.0, 3.0, src));
        list.add(new GreenItData("Ordinateur portable", 200.0, 40.0, 50.0, 25.0, src));
        list.add(new GreenItData("Routeur domestique", 30.0, 5.0, 10.0, 6.0, src));
        list.add(new GreenItData("Serveur cloud (partage)", 0.0, 0.0, 1500.0, 750.0, src));
        list.add(new GreenItData("Data center (par utilisateur)", 0.0, 0.0, 200.0, 100.0, src));

        return list;
    }
}



