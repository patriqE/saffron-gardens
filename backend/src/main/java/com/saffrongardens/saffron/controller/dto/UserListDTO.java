package com.saffrongardens.saffron.controller.dto;

public class UserListDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean approved;

    public UserListDTO() {}

    public UserListDTO(Long id, String username, String email, String role, boolean approved) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.approved = approved;
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
}
