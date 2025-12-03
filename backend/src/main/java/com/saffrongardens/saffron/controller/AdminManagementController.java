package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.AdminManagementService;
import com.saffrongardens.saffron.controller.dto.CreateAdminRequest;
import com.saffrongardens.saffron.controller.dto.UsernameRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminManagementController {

    private final AdminManagementService adminService;

    public AdminManagementController(AdminManagementService adminService) {
        this.adminService = adminService;
    }

    // Create a normal admin
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest req) {
        return ResponseEntity.ok(adminService.createAdmin(req.getUsername(), req.getPassword()));
    }

    // Create a super admin
    @PostMapping("/create-super")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createSuperAdmin(@Valid @RequestBody CreateAdminRequest req) {
        return ResponseEntity.ok(adminService.createSuperAdmin(req.getUsername(), req.getPassword()));
    }

    // Promote existing user to super admin
    @PostMapping("/promote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> promote(@Valid @RequestBody UsernameRequest req) {
        adminService.promoteToSuperAdmin(req.getUsername());
        return ResponseEntity.ok().build();
    }

    // Demote a super admin to admin
    @PostMapping("/demote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> demote(@Valid @RequestBody UsernameRequest req) {
        adminService.demoteToAdmin(req.getUsername());
        return ResponseEntity.ok().build();
    }

    // Delete admin or super admin
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String username) {
        adminService.deleteAdminOrSuperAdmin(username);
        return ResponseEntity.noContent().build();
    }
}
