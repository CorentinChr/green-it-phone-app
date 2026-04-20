package com.uphf.sae6_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uphf.sae6_app.model.GreenItData;

import java.util.List;

/**
 * Adapter simple pour afficher les données Green IT.
 * Le niveau utilisateur (beginner/intermediate/advanced) contrôle la quantité d'informations affichées.
 */
public class DataVizAdapter extends RecyclerView.Adapter<DataVizAdapter.Holder> {

    private final List<GreenItData> items;
    private final String level;
    private String category; // "Fabrication" or "Usage"
    private final LayoutInflater inflater;

    public DataVizAdapter(Context ctx, List<GreenItData> items, String level, String category) {
        this.items = items;
        this.level = level == null ? "beginner" : level;
        this.category = category == null ? "fabrication" : category;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_data_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        GreenItData d = items.get(position);
        holder.tvDevice.setText(d.device);
        holder.tvCo2Mfg.setText(String.format(java.util.Locale.getDefault(), "%.2f", d.co2ManufacturingKg));
        holder.tvEnergyMfg.setText(String.format(java.util.Locale.getDefault(), "%.1f", d.energyManufacturingKwh));
        holder.tvEnergyUse.setText(String.format(java.util.Locale.getDefault(), "%.1f", d.energyUseKwhPerYear));
        holder.tvCo2Use.setText(String.format(java.util.Locale.getDefault(), "%.2f", d.co2UseKgPerYear));
        holder.tvSource.setText(d.source);

        // Contrôle d'affichage selon niveau
        boolean isBeginner = "beginner".equals(level);
        boolean isIntermediate = "intermediate".equals(level);

        boolean showManufacturing = "fabrication".equalsIgnoreCase(category);
        boolean showUsage = "usage".equalsIgnoreCase(category);

        // Beginner: summary only (device + main metric)
        // Intermediate: more metrics
        // Advanced: all available

        if (showManufacturing) {
            // Manufacturing view: show CO2 manufacturing for beginner, add energy for intermediate, show source for advanced
            holder.label_mfg.setVisibility(View.VISIBLE);
            holder.tvCo2Mfg.setVisibility(View.VISIBLE);
            holder.label_mfg_energy.setVisibility(isBeginner ? View.GONE : View.VISIBLE);
            holder.tvEnergyMfg.setVisibility(isBeginner ? View.GONE : View.VISIBLE);

            holder.label_use_energy.setVisibility(View.GONE);
            holder.tvEnergyUse.setVisibility(View.GONE);
            holder.label_use_co2.setVisibility(View.GONE);
            holder.tvCo2Use.setVisibility(View.GONE);
        } else if (showUsage) {
            // Usage view: show energy and usage CO2 depending on level
            holder.label_mfg.setVisibility(View.GONE);
            holder.tvCo2Mfg.setVisibility(View.GONE);
            holder.label_mfg_energy.setVisibility(View.GONE);
            holder.tvEnergyMfg.setVisibility(View.GONE);

            holder.label_use_energy.setVisibility(View.VISIBLE);
            holder.tvEnergyUse.setVisibility(View.VISIBLE);
            holder.label_use_co2.setVisibility(isBeginner ? View.GONE : View.VISIBLE);
            holder.tvCo2Use.setVisibility(isBeginner ? View.GONE : View.VISIBLE);
        }

        // Source visibility: advanced only
        holder.tvSource.setVisibility("advanced".equals(level) ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvDevice;
        TextView tvCo2Mfg;
        TextView tvEnergyMfg;
        TextView tvEnergyUse;
        TextView tvCo2Use;
        TextView tvSource;
        TextView label_mfg;
        TextView label_mfg_energy;
        TextView label_use_energy;
        TextView label_use_co2;

        Holder(@NonNull View v) {
            super(v);
            tvDevice = v.findViewById(R.id.tv_device);
            tvCo2Mfg = v.findViewById(R.id.tv_co2_mfg);
            tvEnergyMfg = v.findViewById(R.id.tv_energy_mfg);
            tvEnergyUse = v.findViewById(R.id.tv_energy_use);
            tvCo2Use = v.findViewById(R.id.tv_co2_use);
            tvSource = v.findViewById(R.id.tv_source);

            label_mfg = v.findViewById(R.id.label_mfg);
            label_mfg_energy = v.findViewById(R.id.label_mfg_energy);
            label_use_energy = v.findViewById(R.id.label_use_energy);
            label_use_co2 = v.findViewById(R.id.label_use_co2);
        }
    }
}



