package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.controller.dto.AccessRequestDTO;
import com.saffrongardens.saffron.entity.AccessRequest;
import com.saffrongardens.saffron.repository.AccessRequestRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class AccessRequestService {
    private final AccessRequestRepository repository;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(AccessRequestService.class);

    @Autowired
    public AccessRequestService(AccessRequestRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public AccessRequest create(AccessRequest request) {
        request.setStatus(AccessRequest.Status.PENDING);
        AccessRequest saved = repository.save(request);
        log.debug("Created AccessRequest id={}", saved.getId());
        return saved;
    }

    public AccessRequest getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AccessRequest not found"));
    }

    public List<AccessRequest> listPending() {
        return repository.findByStatus(AccessRequest.Status.PENDING);
    }

    public AccessRequest approve(Long id, Long approverId) {
        AccessRequest ar = getById(id);
        if (ar.getStatus() != AccessRequest.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending requests can be approved");
        }
        ar.setStatus(AccessRequest.Status.APPROVED);
        ar.setReviewedBy(approverId);
        ar.setReviewedAt(OffsetDateTime.now());
        return repository.save(ar);
    }

    public AccessRequest reject(Long id, Long approverId, String reason) {
        AccessRequest ar = getById(id);
        if (ar.getStatus() != AccessRequest.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending requests can be rejected");
        }
        ar.setStatus(AccessRequest.Status.REJECTED);
        ar.setReviewedBy(approverId);
        ar.setReviewedAt(OffsetDateTime.now());
        ar.setRejectionReason(reason);
        return repository.save(ar);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AccessRequest not found");
        }
        repository.deleteById(id);
        log.debug("Deleted AccessRequest id={}", id);
    }

    public AccessRequest createFromDto(AccessRequestDTO dto) {
        // normalize inputs
        String email = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : null;
        String igRaw = dto.getIgProfile() != null ? dto.getIgProfile().trim() : null;
        String ig = normalizeInstagramProfile(igRaw);

        if (ig == null || ig.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "instagram profile (igProfile) is required");
        }

        // Check duplicates: block if there's already a PENDING request for this email/ig OR if a User exists with this email
        if (email != null) {
            // If a user already exists with this email, block always
            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already belongs to a registered user");
            }
            // If there's an existing pending access request with this email, block
            if (repository.existsByEmailIgnoreCaseAndStatus(email, AccessRequest.Status.PENDING)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a pending request for this email");
            }
        }

        // For instagram profile: if already used in a pending request, block
        if (repository.existsByInstagramProfileIgnoreCaseAndStatus(ig, AccessRequest.Status.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a pending request for this Instagram profile");
        }

        AccessRequest req = new AccessRequest();
        req.setEmail(email);
        req.setInstagramProfile(ig);
        req.setBusinessName(dto.getBusinessName());
        req.setWebsite(dto.getWebsite());
        req.setFullName(dto.getFullName());
        req.setOtherSocials(dto.getOtherSocials());
        // Ensure role is set; default to VENDOR if DTO doesn't provide one
        try {
            if (dto.getRole() != null && !dto.getRole().isBlank()) {
                req.setRole(AccessRequest.Role.valueOf(dto.getRole().toUpperCase()));
            } else {
                req.setRole(AccessRequest.Role.VENDOR);
            }
        } catch (IllegalArgumentException ignored) {
            req.setRole(AccessRequest.Role.VENDOR);
        }
        return create(req);
    }

    // normalize instagram profile to a consistent form: trim, lowercase, strip trailing slash
    private String normalizeInstagramProfile(String ig) {
        if (ig == null) return null;
        String s = ig.trim();
        if (s.isEmpty()) return null;
        // remove trailing slash
        if (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        // lowercase for comparison
        s = s.toLowerCase();
        return s;
    }

}