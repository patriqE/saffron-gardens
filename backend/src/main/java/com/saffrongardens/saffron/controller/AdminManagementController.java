package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.AdminManagementService;
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
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return ResponseEntity.ok(adminService.createAdmin(username, password));
    }

    // Create a super admin
    @PostMapping("/create-super")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createSuperAdmin(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return ResponseEntity.ok(adminService.createSuperAdmin(username, password));
    }

    // Promote existing user to super admin
    @PostMapping("/promote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> promote(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        adminService.promoteToSuperAdmin(username);
        return ResponseEntity.ok().build();
    }

    // Demote a super admin to admin
    @PostMapping("/demote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> demote(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        adminService.demoteToAdmin(username);
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
