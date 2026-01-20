package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.AccessRequestDTO;
import com.saffrongardens.saffron.entity.AccessRequest;
import com.saffrongardens.saffron.repository.AccessRequestRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccessRequestController {

    private final AccessRequestRepository requestRepo;
    private final UserRepository userRepo;

    public AccessRequestController(AccessRequestRepository requestRepo, UserRepository userRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
    }

    // Public registration endpoint as requested: POST /api/register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AccessRequestDTO dto) {
        // Basic validation: email is validated by DTO; ensure instagram profile is present
        if (dto.getIgProfile() == null || dto.getIgProfile().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "instagram profile (igProfile) is required"));
        }

        // Check for existing registration or user with the same email or instagram profile
        if (requestRepo.findByEmail(dto.getEmail()).isPresent() || userRepo.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use or request already exists"));
        }
        if (requestRepo.existsByInstagramProfile(dto.getIgProfile())) {
            return ResponseEntity.status(409).body(Map.of("error", "Instagram profile already in use or request already exists"));
        }

        // create an access request record for admin review/audit (do not create User yet)
        AccessRequest req = new AccessRequest();
        req.setEmail(dto.getEmail());
        // map IG/profile field
        req.setInstagramProfile(dto.getIgProfile().trim());

        // map role string to enum (support VENDOR, EVENT_PLANNER, PLANNER)
        String roleStr = dto.getRole();
        AccessRequest.Role roleEnum = null;
        if (roleStr != null) {
            try {
                // accept both EVENT_PLANNER and PLANNER values
                if (roleStr.equalsIgnoreCase("EVENT_PLANNER") || roleStr.equalsIgnoreCase("PLANNER")) {
                    roleEnum = AccessRequest.Role.PLANNER;
                } else {
                    roleEnum = AccessRequest.Role.valueOf(roleStr.toUpperCase());
                }
            } catch (IllegalArgumentException ex) {
                // leave roleEnum null and set default below
            }
        }
        if (roleEnum == null) roleEnum = AccessRequest.Role.VENDOR; // default
        req.setRole(roleEnum);

        // vendor/planner specific fields
        req.setBusinessName(dto.getBusinessName());
        req.setWebsite(dto.getWebsite());
        req.setFullName(dto.getFullName());
        req.setOtherSocials(dto.getOtherSocials());

        requestRepo.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "requestId", req.getId(),
                "email", req.getEmail(),
                "status", req.getStatus().name()
        ));
    }
}
