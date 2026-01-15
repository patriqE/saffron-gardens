package com.saffrongardens.saffron.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "ig_profile")
    private String igProfile;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role_requested", nullable = false)
    private String roleRequested; // VENDOR or EVENT_PLANNER

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "notes")
    private String notes;

    // Vendor-specific
    @Column(name = "business_name")
    private String businessName;

    @Column(name = "website")
    private String website;

    // Planner-specific
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "other_socials")
    private String otherSocials;

    public RegistrationRequest() {}

    public RegistrationRequest(String username, String igProfile, String passwordHash, String roleRequested) {
        this.username = username;
        this.igProfile = igProfile;
        this.passwordHash = passwordHash;
        this.roleRequested = roleRequested;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIgProfile() {
        return igProfile;
    }

    public void setIgProfile(String igProfile) {
        this.igProfile = igProfile;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoleRequested() {
        return roleRequested;
    }

    public void setRoleRequested(String roleRequested) {
        this.roleRequested = roleRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOtherSocials() {
        return otherSocials;
    }

    public void setOtherSocials(String otherSocials) {
        this.otherSocials = otherSocials;
    }
}
