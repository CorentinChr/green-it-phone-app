package com.uphf.sae6_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private ImageView infoImage;
    private TextView infoTitle;
    private TextView infoContent;
    private TextView infoCounter;
    private Button btnNext;

    private List<InfoItem> items = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        infoImage = findViewById(R.id.info_image);
        infoTitle = findViewById(R.id.info_title);
        infoContent = findViewById(R.id.info_content);
        infoCounter = findViewById(R.id.info_counter);
        btnNext = findViewById(R.id.btn_next_info);

        // Charger des fiches d'exemple
        loadSampleInfos();

        // Afficher première fiche
        if (!items.isEmpty()) {
            currentIndex = 0;
            displayCurrent();
        } else {
            infoTitle.setText("Aucune fiche disponible.");
            infoContent.setText("");
            btnNext.setEnabled(false);
        }

        btnNext.setOnClickListener(v -> onNext());
    }

    private void loadSampleInfos() {
        items.add(new InfoItem(
                "Réduire sa consommation électrique",
                "Éteignez les appareils en veille et privilégiez des appareils économes pour réduire votre consommation d'énergie.",
                "placeholder"));

        items.add(new InfoItem(
                "Recycler le verre",
                "Le verre est recyclable indéfiniment. Rincez les contenants et déposez-les dans la filière adaptée.",
                "placeholder"));

        items.add(new InfoItem(
                "Nettoyage des e-mails",
                "Supprimez régulièrement vos anciens e-mails pour réduire l'espace de stockage et l'empreinte associée.",
                null));

        items.add(new InfoItem(
                "Favoriser les transports actifs",
                "La marche et le vélo sont des alternatives durables pour les trajets courts, réduisant émissions et pollution.",
                "placeholder"));
    }

    private void displayCurrent() {
        InfoItem it = items.get(currentIndex);
        infoTitle.setText(it.title != null ? it.title : "");
        infoContent.setText(it.content != null ? it.content : "");

        // Image
        if (it.imageName != null && !it.imageName.isEmpty()) {
            int resId = getResources().getIdentifier(it.imageName, "drawable", getPackageName());
            if (resId != 0) {
                infoImage.setImageResource(resId);
                infoImage.setVisibility(View.VISIBLE);
            } else {
                infoImage.setVisibility(View.GONE);
            }
        } else {
            infoImage.setVisibility(View.GONE);
        }

        // Compteur
        infoCounter.setText(String.format("%d / %d", currentIndex + 1, items.size()));

        // Bouton Suivant
        if (currentIndex < items.size() - 1) {
            btnNext.setText("Suivant");
        } else {
            btnNext.setText("Terminé");
        }
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
