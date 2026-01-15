package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.RequestRegistrationDTO;
import com.saffrongardens.saffron.entity.RegistrationRequest;
import com.saffrongardens.saffron.repository.RegistrationRequestRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationRequestRepository requestRepo;
    private final UserRepository userRepo;

    public RegistrationController(RegistrationRequestRepository requestRepo, UserRepository userRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
    }

    // Public registration endpoint as requested: POST /api/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RequestRegistrationDTO dto) {
        // Check for existing registration or user with the same email
        if (requestRepo.findByEmail(dto.getEmail()).isPresent() || userRepo.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use or request already exists"));
        }

        // create a registration request record for admin review/audit (do not create User yet)
        RegistrationRequest req = new RegistrationRequest();
        req.setEmail(dto.getEmail());
        req.setIgProfile(dto.getIgProfile());
        req.setRoleRequested(dto.getRole());
        req.setBusinessName(dto.getBusinessName());
        req.setWebsite(dto.getWebsite());
        req.setFullName(dto.getFullName());
        req.setOtherSocials(dto.getOtherSocials());
        requestRepo.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "requestId", req.getId(),
                "email", req.getEmail(),
                "status", req.getStatus()
        ));
    }
}
