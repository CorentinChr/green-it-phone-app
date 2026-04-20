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
        // Champs additionnels pour mapper la réponse JSON plate (format reçu de l'API)
        public Integer id;
        public String type; // e.g. "TEXT" or "QUIZ"
        public String question;
        public java.util.List<String> answers;
        public Integer correctIndex;

        public InfoStep(String text, String imageName) {
            this.text = text;
            this.imageName = imageName;
        }
        public InfoStep(Quiz quiz) {
            this.quiz = quiz;
        }

        /**
         * Assure que le champ `quiz` est initialisé aussi bien pour le format
         * historique (où `quiz` était un objet) que pour le format plat renvoyé
         * par l'API (où les propriétés du quiz sont au même niveau dans l'étape).
         */
        public void ensureQuiz() {
            if (this.quiz == null && "QUIZ".equalsIgnoreCase(this.type) && this.question != null) {
                int idx = this.correctIndex != null ? this.correctIndex : 0;
                java.util.List<String> ans = this.answers != null ? this.answers : new java.util.ArrayList<>();
                this.quiz = new Quiz(this.question, ans, idx);
            }
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
