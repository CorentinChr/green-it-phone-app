package com.uphf.sae6_app.retrofit.dto;

import java.util.List;

public class UserResponse {
    public String userId;
    public String name;
    public String level;
    public int score;
    public List<Integer> scoreHistory;
    public String lastUpdated; // optional depending on backend

    public UserResponse() {}
}

