package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    Optional<AccessRequest> findByEmail(String email);
    Optional<AccessRequest> findByInstagramProfile(String instagramProfile);
    List<AccessRequest> findByStatus(AccessRequest.Status status);
    boolean existsByEmail(String email);
    boolean existsByInstagramProfile(String instagramProfile);

    // Case-insensitive helpers to avoid false negatives due to casing
    boolean existsByEmailIgnoreCase(String email);
    Optional<AccessRequest> findByEmailIgnoreCase(String email);
    boolean existsByInstagramProfileIgnoreCase(String instagramProfile);

    // Helpers to check for an existing request with a specific status (e.g. PENDING)
    boolean existsByEmailIgnoreCaseAndStatus(String email, AccessRequest.Status status);
    boolean existsByInstagramProfileIgnoreCaseAndStatus(String instagramProfile, AccessRequest.Status status);
}