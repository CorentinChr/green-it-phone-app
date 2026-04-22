package com.uphf.sae6_app.data.retrofit.dto;

import java.util.List;

public class UserRequest {
    public String userId;
    public String name;
    public String level;
    public int score;
    public List<Integer> scoreHistory;
    public List<Integer> qhScoreHistory; // quiz habitudes numériques
    public List<Integer> arScoreHistory; // quiz application AR

    public UserRequest() {}

    public UserRequest(String userId, String name, String level, int score, List<Integer> scoreHistory) {
        this.userId = userId;
        this.name = name;
        this.level = level;
        this.score = score;
        this.scoreHistory = scoreHistory;
    }

    // Nouvelle surcharge acceptant les historiques supplémentaires (qh et ar).
    public UserRequest(String userId, String name, String level, int score, List<Integer> scoreHistory, List<Integer> qhScoreHistory, List<Integer> arScoreHistory) {
        this.userId = userId;
        this.name = name;
        this.level = level;
        this.score = score;
        this.scoreHistory = scoreHistory;
        this.qhScoreHistory = qhScoreHistory;
        this.arScoreHistory = arScoreHistory;
    }
}

