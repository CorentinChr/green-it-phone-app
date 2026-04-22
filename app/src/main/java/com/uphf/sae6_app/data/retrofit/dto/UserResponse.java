package com.uphf.sae6_app.data.retrofit.dto;

import java.util.List;

public class UserResponse {
    public String userId;
    public String name;
    public String level;
    public int score;
    public List<Integer> scoreHistory;
    public List<Integer> qhScoreHistory; // quiz habitudes numériques
    public List<Integer> arScoreHistory; // quiz application AR
    public String lastUpdated; // optional depending on backend

    public UserResponse() {}
}

