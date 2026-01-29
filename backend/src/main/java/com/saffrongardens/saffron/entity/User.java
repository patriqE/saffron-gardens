package com.saffrongardens.saffron.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // canonical password hash column (used by DB migrations)
    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // older/alternate column that may exist in some DB states; keep in sync
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private String role; //ADMIN, EVENT_PLANNER, VENDOR

    private boolean approved = false; //false if not approved by admin

    // New flag to indicate admin allowed the user to continue profile completion
    private boolean canCompleteProfile = false;

    @Column(name = "email")
    private String email;

    public User() {
    }

    public User(String username, String passwordHashOrRaw, String role) {
        this.username = username;
        // caller should pass an already-encoded hash. We store it to both columns to keep DBs in sync
        this.passwordHash = passwordHashOrRaw;
        this.password = passwordHashOrRaw;
        this.role = role;
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

    // Spring Security will call getPassword(); return the canonical hash
    public String getPassword() {
        return passwordHash;
    }

    // set both columns to keep them in sync
    public void setPassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.password = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.password = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isCanCompleteProfile() {
        return canCompleteProfile;
    }

    public void setCanCompleteProfile(boolean canCompleteProfile) {
        this.canCompleteProfile = canCompleteProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
