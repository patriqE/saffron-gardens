package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.RequestRegistrationDTO;
import com.saffrongardens.saffron.entity.RegistrationRequest;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.RegistrationRequestRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(RegistrationRequestRepository requestRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Public registration endpoint as requested: POST /api/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RequestRegistrationDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }
        // ensure username not taken
        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Username already exists"));
        }
        // create user directly with pending flags
        String hashed = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getUsername(), hashed, dto.getRole());
        user.setApproved(false);
        user.setCanCompleteProfile(false);
        user = userRepo.save(user);

        // also store a registration request record for admin review/audit
        RegistrationRequest req = new RegistrationRequest(dto.getUsername(), dto.getIgProfile(), hashed, dto.getRole());
        requestRepo.save(req);

        return ResponseEntity.status(201).body(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "approved", user.isApproved(),
                "canComplete", user.isCanCompleteProfile()
        ));
    }
}
