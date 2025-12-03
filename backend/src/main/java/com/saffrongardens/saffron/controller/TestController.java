package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.AdminManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final AdminManagementService adminService;

    @Value("${app.test.secret:e2e-test-secret}")
    private String testSecret;

    public TestController(AdminManagementService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/seed-admin")
    public ResponseEntity<?> seedAdmin(@RequestHeader(value = "X-TEST-SECRET", required = false) String headerSecret,
                                       @RequestBody Map<String, String> body) {
        if (testSecret == null || !testSecret.equals(headerSecret)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        String username = body.getOrDefault("username","e2e-admin");
        String password = body.getOrDefault("password","password");
        // create as SUPER_ADMIN so tests can create other users
        adminService.createSuperAdmin(username, password);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
