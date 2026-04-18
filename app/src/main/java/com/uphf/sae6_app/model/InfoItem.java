package com.uphf.sae6_app.model;

import java.util.List;

/**
 * Modèle simple pour une fiche d'information.
 */
public class InfoItem {
    public String title;
    public String content;
    public String imageName;

    public int difficulty; // 1=facile, 2=moyen, 3=difficile
    public String theme;

    public List<InfoStep> steps;

    public InfoItem() { }

    public InfoItem(String title, String content, String imageName, int difficulty, String theme) {
        this.title = title;
        this.content = content;
        this.imageName = imageName;
        this.difficulty = difficulty;
        this.theme = theme;
    }

    public InfoItem(String title, int difficulty, String theme, List<InfoStep> steps) {
        this.title = title;
        this.difficulty = difficulty;
        this.theme = theme;
        this.steps = steps;
    }

    // Classe interne pour une étape interactive
    public static class InfoStep {
        public String text;
        public String imageName;
        public Quiz quiz;

        public InfoStep(String text, String imageName) {
            this.text = text;
            this.imageName = imageName;
        }
        public InfoStep(Quiz quiz) {
            this.quiz = quiz;
        }
    }

    // Classe interne pour un quiz dynamique
    public static class Quiz {
        public String question;
        public List<String> answers;
        public int correctIndex;

        public Quiz(String question, List<String> answers, int correctIndex) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
        }
    }
}
