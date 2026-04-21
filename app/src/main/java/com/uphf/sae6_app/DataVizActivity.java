package com.uphf.sae6_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uphf.sae6_app.model.GreenItData;
import android.widget.Toast;
import com.uphf.sae6_app.retrofit.RetrofitClient;
import com.uphf.sae6_app.retrofit.GreenItApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private DataVizAdapter adapter;
    private String currentCategory;

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

        // Initialement liste vide — les données seront chargées depuis l'API
        List<GreenItData> items = new ArrayList<>();

        // Préparer la catégorie "fabrication" comme cible par défaut si elle existe
        String[] categories = getResources().getStringArray(R.array.data_categories_values);
        int fabricationIndex = 0;
        for (int i = 0; i < categories.length; i++) {
            if ("fabrication".equalsIgnoreCase(categories[i])) {
                fabricationIndex = i;
                break;
            }
        }

        // Créer un adapter initial (vide pour l'instant) — on utilisera le listener du Spinner
        // retenir la catégorie courante et créer l'adapter initial
        this.currentCategory = categories[fabricationIndex];
        this.adapter = new DataVizAdapter(this, items, userLevel, this.currentCategory);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(this.adapter);

        // Charger données depuis l'API et notifier l'adapter (utilise le champ adapter)
        loadGreenItDataFromApi(items, userLevel);

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
                // recreate adapter with new category and keep reference in the activity
                DataVizActivity.this.currentCategory = cat;
                DataVizAdapter newAdapter = new DataVizAdapter(DataVizActivity.this, items, userLevel, DataVizActivity.this.currentCategory);
                DataVizActivity.this.adapter = newAdapter;
                rvData.setAdapter(DataVizActivity.this.adapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Positionner la sélection du spinner après avoir attaché le listener
        // pour forcer l'exécution de la logique d'initialisation (description + adapter)
        spinner.setSelection(fabricationIndex);

        // Certaines configurations de Spinner n'appellent pas onItemSelected lors du setSelection
        // si la sélection n'a pas changé. Pour garantir l'initialisation de l'affichage,
        // on déclenche explicitement la même logique que dans le listener ici.
        String initialCat = categories[fabricationIndex];
        if ("fabrication".equalsIgnoreCase(initialCat)) {
            tvCatDesc.setText(getString(R.string.desc_manufacturing));
        } else {
            tvCatDesc.setText(getString(R.string.desc_usage));
        }
        // recréer l'adapter pour la catégorie initiale afin d'afficher les bonnes données
        this.currentCategory = initialCat;
        DataVizAdapter initialAdapter = new DataVizAdapter(DataVizActivity.this, items, userLevel, this.currentCategory);
        this.adapter = initialAdapter;
        rvData.setAdapter(this.adapter);

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

    private void loadGreenItDataFromApi(List<GreenItData> items, String userLevel) {
        GreenItApi api = RetrofitClient.getInstance().create(GreenItApi.class);
        api.getGreenItData().enqueue(new Callback<java.util.List<GreenItData>>() {
            @Override
            public void onResponse(Call<java.util.List<GreenItData>> call, Response<java.util.List<GreenItData>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(DataVizActivity.this, "Erreur API données Green IT", Toast.LENGTH_LONG).show();
                    return;
                }
                items.clear();
                items.addAll(response.body());
                // recréer l'adapter courant avec les données reçues pour s'assurer
                // que le filtrage/parcours interne de l'adapter est appliqué
                if (DataVizActivity.this.currentCategory != null) {
                    DataVizAdapter refreshed = new DataVizAdapter(DataVizActivity.this, items, userLevel, DataVizActivity.this.currentCategory);
                    DataVizActivity.this.adapter = refreshed;
                    DataVizActivity.this.rvData.setAdapter(refreshed);
                } else {
                    // fallback: notifier si on a un adapter
                    if (DataVizActivity.this.adapter != null) {
                        DataVizActivity.this.adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<java.util.List<GreenItData>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DataVizActivity.this, "Erreur réseau données: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}



