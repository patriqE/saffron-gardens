package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.AssignmentStatus;
import com.saffrongardens.saffron.entity.VendorAssignment;
import com.saffrongardens.saffron.repository.VendorAssignmentRepository;
import com.saffrongardens.saffron.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class VendorAssignmentController {

    private final VendorAssignmentRepository assignmentRepo;
    private final AuditService auditService;

    public VendorAssignmentController(VendorAssignmentRepository assignmentRepo, AuditService auditService) {
        this.assignmentRepo = assignmentRepo;
        this.auditService = auditService;
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> accept(@PathVariable Long id, Authentication auth) {
        Optional<VendorAssignment> opt = assignmentRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        VendorAssignment a = opt.get();

        if (a.getVendor() == null) return ResponseEntity.status(404).body("Vendor not assigned");

        if (!a.getVendor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).body("Not allowed");
        }

        a.setStatus(AssignmentStatus.CONFIRMED);
        assignmentRepo.save(a);
        auditService.record(auth.getName(), "ASSIGNMENT_ACCEPTED", "assignmentId=" + a.getId());
        return ResponseEntity.ok(a);
    }

    @PostMapping("/{id}/decline")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> decline(@PathVariable Long id, Authentication auth) {
        Optional<VendorAssignment> opt = assignmentRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        VendorAssignment a = opt.get();

        if (a.getVendor() == null) return ResponseEntity.status(404).body("Vendor not assigned");

        if (!a.getVendor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).body("Not allowed");
        }

        a.setStatus(AssignmentStatus.CANCELLED);
        assignmentRepo.save(a);
        auditService.record(auth.getName(), "ASSIGNMENT_DECLINED", "assignmentId=" + a.getId());
        return ResponseEntity.ok(a);
    }
}
