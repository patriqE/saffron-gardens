package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.AdminManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminManagementService adminManagementService;


    @PostMapping("/planner/approve/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> approvePlanner(@PathVariable Long userId) {
        adminManagementService.approveUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
        String actor = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM";
        try {
            var saved = adminManagementService.confirmBooking(bookingId, actor);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
