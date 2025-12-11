package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.VendorService;
import com.saffrongardens.saffron.service.AdminManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private AdminManagementService adminManagementService;

    @PostMapping("/vendor/approve/{vendorId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> approveVendor(@PathVariable Long vendorId) {
        vendorService.approveVendor(vendorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/planner/approve/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> approvePlanner(@PathVariable Long userId) {
        adminManagementService.approveUser(userId);
        return ResponseEntity.noContent().build();
    }
}
