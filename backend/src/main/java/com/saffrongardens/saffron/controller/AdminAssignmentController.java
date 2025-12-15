package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.VendorAssignment;
import com.saffrongardens.saffron.repository.VendorAssignmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assignments")
public class AdminAssignmentController {

    private final VendorAssignmentRepository vendorAssignmentRepository;

    public AdminAssignmentController(VendorAssignmentRepository vendorAssignmentRepository) {
        this.vendorAssignmentRepository = vendorAssignmentRepository;
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        VendorAssignment a = vendorAssignmentRepository.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();
        a.setStatus(com.saffrongardens.saffron.entity.AssignmentStatus.CONFIRMED);
        vendorAssignmentRepository.save(a);
        return ResponseEntity.noContent().build();
    }
}
