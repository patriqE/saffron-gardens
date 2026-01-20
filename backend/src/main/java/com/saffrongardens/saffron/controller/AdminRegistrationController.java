package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.AccessRequest;
import com.saffrongardens.saffron.service.AccessRequestService;
import com.saffrongardens.saffron.service.AuditService;
import com.saffrongardens.saffron.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/registration")
public class AdminRegistrationController {

    private final AccessRequestService accessRequestService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public AdminRegistrationController(AccessRequestService accessRequestService, AuditService auditService, NotificationService notificationService) {
        this.accessRequestService = accessRequestService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<List<AccessRequest>> listPending() {
        List<AccessRequest> pending = accessRequestService.listPending();
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        // actor (username) performing the approval
        String actor = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM";

        AccessRequest req = accessRequestService.approve(id, null);

        auditService.record(actor, "APPROVE_REGISTRATION_REQUEST", "Approved registration request for: " + req.getEmail());

        // Notify the requester that they can now complete their application
        notificationService.sendCanCompleteNotification(req.getEmail(), req.getRole().name());

        return ResponseEntity.ok(Map.of("requestId", req.getId(), "email", req.getEmail()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String actor = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM";

        String note = body != null ? body.getOrDefault("note", null) : null;
        AccessRequest req = accessRequestService.reject(id, null, note);

        auditService.record(actor, "REJECT_REGISTRATION_REQUEST", "Rejected registration request for: " + req.getEmail());
        return ResponseEntity.ok().build();
    }
}
