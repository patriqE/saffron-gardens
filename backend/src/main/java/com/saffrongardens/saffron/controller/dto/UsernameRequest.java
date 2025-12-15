package com.saffrongardens.saffron.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class UsernameRequest {

    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
