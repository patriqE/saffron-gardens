package com.saffrongardens.saffron.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestRegistrationDTO {

    @NotBlank
    private String username;

    private String igProfile;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String role; // VENDOR or EVENT_PLANNER

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIgProfile() {
        return igProfile;
    }

    public void setIgProfile(String igProfile) {
        this.igProfile = igProfile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

