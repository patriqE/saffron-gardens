package com.saffrongardens.saffron.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AccessRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String igProfile;

    @NotBlank
    private String role; // VENDOR or EVENT_PLANNER or PLANNER

    // Vendor-specific
    private String businessName;
    private String website; // optional

    // Planner-specific
    private String fullName;
    private String otherSocials; // optional

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIgProfile() { return igProfile; }
    public void setIgProfile(String igProfile) { this.igProfile = igProfile; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getOtherSocials() { return otherSocials; }
    public void setOtherSocials(String otherSocials) { this.otherSocials = otherSocials; }
}
