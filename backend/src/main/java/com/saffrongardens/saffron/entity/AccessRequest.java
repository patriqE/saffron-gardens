package com.saffrongardens.saffron.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "access_request",
        indexes = {
                @Index(name = "idx_access_request_status", columnList = "status"),
                @Index(name = "idx_access_request_role", columnList = "role"),
                @Index(name = "idx_access_request_email", columnList = "email")
        }
)
public class AccessRequest {

    public enum Role {
        VENDOR,
        PLANNER
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email used to request access.
     * NOT unique to allow re-requests after rejection.
     */
    @Column(nullable = false, length = 255)
    private String email;

    /**
     * Requested role.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    /**
     * Vendor-specific field.
     * Nullable for planners.
     */
    @Column(name = "business_name", length = 255)
    private String businessName;

    /**
     * Optional website (mostly vendors).
     */
    @Column(length = 512)
    private String website;

    /**
     * Planner-specific field.
     * Nullable for vendors.
     */
    @Column(name = "full_name", length = 255)
    private String fullName;

    /**
     * Other social links (optional).
     */
    @Column(name = "other_socials", columnDefinition = "TEXT")
    private String otherSocials;

    /**
     * Instagram profile (required).
     */
    @Column(name = "instagram_profile", nullable = false, length = 255)
    private String instagramProfile;

    /**
     * Current request status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status = Status.PENDING;

    /**
     * Admin (user ID) who reviewed the request.
     */
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    /**
     * Timestamp when request was reviewed.
     */
    @Column(name = "reviewed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime reviewedAt;

    /**
     * Reason for rejection (required when status = REJECTED).
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Creation timestamp.
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    /**
     * Optimistic locking to prevent double approvals.
     */
    @Version
    private Long version;

    public AccessRequest() {}

    /* =====================
       Lifecycle callbacks
       ===================== */

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.status = Status.PENDING;
    }

    @PreUpdate
    protected void validateState() {
        if (status == Status.REJECTED && (rejectionReason == null || rejectionReason.isBlank())) {
            throw new IllegalStateException("Rejection reason is required when status is REJECTED");
        }
    }

    /* =====================
       Getters and setters
       ===================== */

    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getOtherSocials() { return otherSocials; }
    public void setOtherSocials(String otherSocials) { this.otherSocials = otherSocials; }

    public String getInstagramProfile() { return instagramProfile; }
    public void setInstagramProfile(String instagramProfile) { this.instagramProfile = instagramProfile; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }

    public OffsetDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(OffsetDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
