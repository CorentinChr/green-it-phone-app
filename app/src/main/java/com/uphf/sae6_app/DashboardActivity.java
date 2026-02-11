package com.uphf.sae6_app;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private SeekBar seekbarCleanInterval;
    private TextView txtIntervalValue;
    private SwitchCompat switchUnsubscribe;
    private TextView txtImpactValue;
    private ProgressBar progressImpact;

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
        double co2PerMailPerYear = 0.0004; // kg CO2e
        double mailsPerDay = 20.0; // nombre moyen de mails reçus par jour

        // si désabonné, réduire mails reçus
        if (unsubscribed) mailsPerDay *= 0.7; // -30%

        // Estimation du stock moyen de mails conservés entre nettoyages
        // si on nettoie tous les N jours, stock moyen ≈ mailsPerDay * N / 2
        double avgSavedMails = mailsPerDay * daysBetweenClean / 2.0;

        double annualCo2 = avgSavedMails * co2PerMailPerYear * 365.0; // kg CO2e/an

        // Mettre à jour l'UI
        String formatted = String.format(Locale.getDefault(), "%.2f kg CO2e / an", annualCo2);
        txtImpactValue.setText(formatted);

        // Map to progress 0..100 for visualization (on fixe une borne haute arbitraire)
        double maxDisplay = 50.0; // 50 kg CO2e/an = 100%
        int progress = (int) Math.min(100, (annualCo2 / maxDisplay) * 100);
        progressImpact.setProgress(progress);
    }
}
