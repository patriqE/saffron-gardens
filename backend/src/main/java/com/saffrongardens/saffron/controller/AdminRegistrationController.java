package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.RegistrationRequest;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.RegistrationRequestRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/registration")
public class AdminRegistrationController {

    private final RegistrationRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final AuditService auditService;

    public AdminRegistrationController(RegistrationRequestRepository requestRepo, UserRepository userRepo, AuditService auditService) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.auditService = auditService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> listPending() {
        List<RegistrationRequest> pending = requestRepo.findByStatus("PENDING");
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        RegistrationRequest req = requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(req.getStatus())) return ResponseEntity.status(400).body(Map.of("error", "Request already processed"));

        // Try to find existing user created at registration time
        User user = userRepo.findByUsername(req.getUsername()).orElse(null);
        if (user == null) {
            // fallback: create user from request (passwordHash already stored)
            user = new User(req.getUsername(), req.getPasswordHash(), req.getRoleRequested());
        }
        user.setCanCompleteProfile(true);
        user.setApproved(false);
        user = userRepo.save(user);

        req.setStatus("APPROVED");
        req.setProcessedAt(Instant.now());
        req.setProcessedBy(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM");
        requestRepo.save(req);

        auditService.record(req.getProcessedBy(), "APPROVE_REGISTRATION_REQUEST", "Approved registration request for: " + req.getUsername());

        return ResponseEntity.ok(Map.of("userId", user.getId(), "username", user.getUsername()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        RegistrationRequest req = requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(req.getStatus())) return ResponseEntity.status(400).body(Map.of("error", "Request already processed"));
        req.setStatus("REJECTED");
        req.setProcessedAt(Instant.now());
        req.setProcessedBy(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM");
        req.setNotes(body != null ? body.getOrDefault("note", null) : null);
        requestRepo.save(req);
        auditService.record(req.getProcessedBy(), "REJECT_REGISTRATION_REQUEST", "Rejected registration request for: " + req.getUsername());
        return ResponseEntity.ok().build();
    }
}
