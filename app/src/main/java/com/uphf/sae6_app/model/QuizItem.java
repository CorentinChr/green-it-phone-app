package com.uphf.sae6_app.model;

import java.util.List;

/**
 * Modèle représentant une question du quiz.
 * Ajout des champs : difficulty (int) et image (String).
 * - difficulty: 1 = facile, 2 = moyen, 3 = difficile (convention)
 * - image: nom de la ressource drawable (ex: "ic_quiz_image") ou null
 */
public class QuizItem {
    public int id;
    public String question;
    public List<String> answers;
    public int correctIndex;

    // Nouvelle propriété : information affichée lorsqu'on répond correctement
    public String infos;

    // Thème de la question
    public String theme;

    // Difficulte : 1=facile,2=moyen,3=difficile (peut être adapté)
    public int difficulty;

    // Image : nom de la ressource drawable (sans extension) ou null
    public String image;

    public QuizItem() { }

    public QuizItem(int id, String question, List<String> answers, int correctIndex, String infos, String theme, int difficulty, String image) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.correctIndex = correctIndex;
        this.infos = infos;
        this.theme = theme;
        this.difficulty = difficulty;
        this.image = image;
    }

    public String getTheme() {
        return theme;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getImage() {
        return image;
    }
}
