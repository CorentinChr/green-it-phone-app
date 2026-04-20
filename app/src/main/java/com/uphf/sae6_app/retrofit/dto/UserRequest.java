package com.uphf.sae6_app.retrofit.dto;

import java.util.List;

public class UserRequest {
    public String userId;
    public String name;
    public String level;
    public int score;
    public List<Integer> scoreHistory;

    public UserRequest() {}

    public UserRequest(String userId, String name, String level, int score, List<Integer> scoreHistory) {
        this.userId = userId;
        this.name = name;
        this.level = level;
        this.score = score;
        this.scoreHistory = scoreHistory;
    }
}

