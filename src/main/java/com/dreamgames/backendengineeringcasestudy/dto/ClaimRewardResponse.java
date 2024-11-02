package com.dreamgames.backendengineeringcasestudy.dto;

import com.dreamgames.backendengineeringcasestudy.users.model.User;

public class ClaimRewardResponse {
    private String message;
    private User user;


    public ClaimRewardResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClaimRewardResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }
}
