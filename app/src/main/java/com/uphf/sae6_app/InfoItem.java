package com.uphf.sae6_app;

/**
 * Modèle simple pour une fiche d'information.
 */
public class InfoItem {
    public String title;
    public String content;
    public String imageName;

    public InfoItem() { }

    public InfoItem(String title, String content, String imageName) {
        this.title = title;
        this.content = content;
        this.imageName = imageName;
    }
}

