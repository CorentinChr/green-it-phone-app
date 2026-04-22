package com.uphf.sae6_app.ui.activities;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.uphf.sae6_app.R;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private SeekBar seekbarCleanInterval;
    private TextView txtIntervalValue;
    private SwitchCompat switchUnsubscribe;
    private TextView txtImpactValue;
    private ProgressBar progressImpact;
    // Nouveaux contrôles
    private SeekBar seekbarHardware;
    private SeekBar seekbarServices;
    private TextView txtHardwareValue;
    private TextView txtServicesValue;
    private Spinner spinnerQuality;
    private String selectedQuality = "HD"; // valeur par défaut
    private PieChart pieChartCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Views
        seekbarCleanInterval = findViewById(R.id.seekbar_clean_interval);
        txtIntervalValue = findViewById(R.id.txt_interval_value);
        switchUnsubscribe = findViewById(R.id.switch_unsubscribe);
        txtImpactValue = findViewById(R.id.txt_impact_value);
        progressImpact = findViewById(R.id.progressImpact);
        // Nouveaux views
        seekbarHardware = findViewById(R.id.seekbar_hardware);
        seekbarServices = findViewById(R.id.seekbar_services);
        txtHardwareValue = findViewById(R.id.txt_hardware_value);
        txtServicesValue = findViewById(R.id.txt_services_value);
        pieChartCategories = findViewById(R.id.pieChartCategories);
        spinnerQuality = findViewById(R.id.spinner_quality);

        // Config spinner quality
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.streaming_qualities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuality.setAdapter(adapter);
        spinnerQuality.setSelection(1); // HD par défaut (index 1)
        spinnerQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedQuality = parent.getItemAtPosition(position).toString();
                // Mettre à jour l'affichage instantané
                int hours = seekbarServices.getProgress();
                double kg = estimateServicesKg(hours, selectedQuality);
                txtServicesValue.setText(String.format(Locale.getDefault(), "%d h / semaine (≈ %.1f kg/an)", hours, kg));
                int days = seekbarCleanInterval.getProgress() + 1;
                recalcImpact(days, switchUnsubscribe.isChecked());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Config de base du pie chart
        pieChartCategories.getDescription().setEnabled(false);
        pieChartCategories.setUsePercentValues(false);
        pieChartCategories.setDrawHoleEnabled(true);
        pieChartCategories.setHoleColor(Color.TRANSPARENT);
        Legend l = pieChartCategories.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        // Initial values
        int defaultDays = 7;
        seekbarCleanInterval.setProgress(defaultDays - 1); // 0..29 -> represent 1..30
        txtIntervalValue.setText(getString(R.string.interval_example));

        // Listeners
        seekbarCleanInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int days = progress + 1;
                txtIntervalValue.setText(getString(R.string.interval_value_format, days));
                recalcImpact(days, switchUnsubscribe.isChecked());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        switchUnsubscribe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int days = seekbarCleanInterval.getProgress() + 1;
            recalcImpact(days, isChecked);
        });

        // Listeners pour les nouvelles catégories
        seekbarHardware.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtHardwareValue.setText(progress + " appareils / an");
                int days = seekbarCleanInterval.getProgress() + 1;
                recalcImpact(days, switchUnsubscribe.isChecked());
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekbarServices.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress = heures de streaming par semaine
                double kg = estimateServicesKg(progress, selectedQuality);
                txtServicesValue.setText(String.format(Locale.getDefault(), "%d h / semaine (≈ %.1f kg/an)", progress, kg));
                int days = seekbarCleanInterval.getProgress() + 1;
                recalcImpact(days, switchUnsubscribe.isChecked());
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Compute initial impact
        recalcImpact(defaultDays, switchUnsubscribe.isChecked());
    }

    /**
     * Recalcule une estimation simplifiée d'impact CO2e par an en fonction des paramètres.
     * Hypothèses (fictives pour la démo) :
     * - Chaque mail stocké génère 0.0004 kg CO2e/an (valeur fictive)
     * - Nettoyer plus souvent réduit le stock moyen de mails proportionnellement.
     * - Désabonnement réduit de 30% le volume de mails reçus.
     */
    private void recalcImpact(int daysBetweenClean, boolean unsubscribed) {
        // Paramètres fictifs
        double co2PerMailPerYear = 0.0004; // kg CO2e par mail stocké (fictif)
        double mailsPerDay = 20.0; // nombre moyen de mails reçus par jour (base)

        // si désabonné, réduire mails reçus
        if (unsubscribed) mailsPerDay *= 0.7; // -30%

        // Estimation du stock moyen de mails conservés entre nettoyages
        double avgSavedMails = mailsPerDay * daysBetweenClean / 2.0;
        double annualCo2_mails = avgSavedMails * co2PerMailPerYear * 365.0; // kg CO2e/an

        // Matériel — estimation simple : x appareils / an * co2ParAppareil
        int hardwareCount = seekbarHardware != null ? seekbarHardware.getProgress() : 0;
        double co2PerDevice = 50.0; // kg CO2e par appareil (valeur fictive)
        double annualCo2_hardware = hardwareCount * co2PerDevice;

        // Services — interprété comme heures de streaming par semaine
        int hoursPerWeek = seekbarServices != null ? seekbarServices.getProgress() : 0;
        double annualCo2_services = estimateServicesKg(hoursPerWeek, selectedQuality);

        // Total
        double annualCo2 = annualCo2_mails + annualCo2_hardware + annualCo2_services;

        // Mettre à jour l'UI (valeur totale)
        String formatted = String.format(Locale.getDefault(), "%.2f kg CO2e / an", annualCo2);
        txtImpactValue.setText(formatted);

        // Map to progress 0..100 pour visualisation (borne haute arbitraire adaptée)
        double maxDisplay = 500.0; // 500 kg CO2e/an = 100%
        int progress = (int) Math.min(100, (annualCo2 / maxDisplay) * 100);
        progressImpact.setProgress(progress);

        // Mettre à jour le diagramme circulaire (PieChart)
        if (pieChartCategories != null) {
            java.util.ArrayList<PieEntry> entries = new java.util.ArrayList<>();
            if (annualCo2_mails > 0) entries.add(new PieEntry((float) annualCo2_mails, "Mails"));
            if (annualCo2_hardware > 0) entries.add(new PieEntry((float) annualCo2_hardware, "Matériel"));
            if (annualCo2_services > 0) entries.add(new PieEntry((float) annualCo2_services, "Services"));

            if (entries.isEmpty()) {
                pieChartCategories.clear();
                pieChartCategories.setCenterText("Aucune donnée");
                pieChartCategories.invalidate();
            } else {
                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueTextSize(12f);

                PieData data = new PieData(dataSet);
                pieChartCategories.setData(data);
                pieChartCategories.setCenterText(String.format(Locale.getDefault(), "Total: %.2f kg", annualCo2));
                pieChartCategories.invalidate();
            }
        }
    }

    /**
     * Estime les kg CO2/an pour le streaming à partir d'heures/semaine et de la qualité.
     * Retourne la valeur en kg CO2/an.
     */
    private double estimateServicesKg(int hoursPerWeek, String quality) {
        double gbPerHour;
        switch (quality) {
            case "SD": gbPerHour = 1.0; break;
            case "UHD": gbPerHour = 7.0; break;
            case "HD":
            default: gbPerHour = 3.0; break;
        }
        double kgCO2PerGB = 0.06; // kg CO2e par GB transféré (valeur indicative)
        return hoursPerWeek * 52.0 * gbPerHour * kgCO2PerGB;
    }
}
