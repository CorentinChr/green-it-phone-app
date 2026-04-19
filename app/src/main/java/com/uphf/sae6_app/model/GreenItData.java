package com.uphf.sae6_app.model;

/**
 * Modèle de données pour la visualisation Green IT.
 * Les champs sont volontairement simples et prêts à être persistés en base plus tard.
 */
public class GreenItData {
    public String device; // e.g. "Smartphone"
    // Removed category string: a device contains both manufacturing and usage metrics
    public double co2ManufacturingKg; // kg CO2e (fabrication)
    public double energyManufacturingKwh; // kWh (energie consommee pour la fabrication)
    public double energyUseKwhPerYear; // kWh/an (usage)
    public double co2UseKgPerYear; // kg CO2e/an (usage)
    public String source; // e.g. "ADEME 2024"

    public GreenItData() {}

    public GreenItData(String device, double co2ManufacturingKg, double energyManufacturingKwh, double energyUseKwhPerYear, double co2UseKgPerYear, String source) {
        this.device = device;
        this.co2ManufacturingKg = co2ManufacturingKg;
        this.energyManufacturingKwh = energyManufacturingKwh;
        this.energyUseKwhPerYear = energyUseKwhPerYear;
        this.co2UseKgPerYear = co2UseKgPerYear;
        this.source = source;
    }
}


