package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.RegistrationRequest;
import com.saffrongardens.saffron.repository.RegistrationRequestRepository;
import com.saffrongardens.saffron.service.AuditService;
import com.saffrongardens.saffron.service.NotificationService;
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
    private final AuditService auditService;
    private final NotificationService notificationService;

    public AdminRegistrationController(RegistrationRequestRepository requestRepo, AuditService auditService, NotificationService notificationService) {
        this.requestRepo = requestRepo;
        this.auditService = auditService;
        this.notificationService = notificationService;
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

        req.setStatus("APPROVED");
        req.setProcessedAt(Instant.now());
        req.setProcessedBy(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM");
        requestRepo.save(req);

        auditService.record(req.getProcessedBy(), "APPROVE_REGISTRATION_REQUEST", "Approved registration request for: " + req.getEmail());

        // Notify the requester that they can now complete their application
        notificationService.sendCanCompleteNotification(req.getEmail(), req.getRoleRequested());

        return ResponseEntity.ok(Map.of("requestId", req.getId(), "email", req.getEmail()));
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
        auditService.record(req.getProcessedBy(), "REJECT_REGISTRATION_REQUEST", "Rejected registration request for: " + req.getEmail());
        return ResponseEntity.ok().build();
    }
}
