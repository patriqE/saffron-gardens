package com.saffrongardens.saffron.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    public AuditEvent() {}

    public AuditEvent(String username, String action, String details) {
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Instant getTimestamp() { return timestamp; }

    public void setUsername(String username) { this.username = username; }
    public void setAction(String action) { this.action = action; }
    public void setDetails(String details) { this.details = details; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
