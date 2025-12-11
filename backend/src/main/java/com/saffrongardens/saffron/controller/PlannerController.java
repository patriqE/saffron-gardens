package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.CreatePlannerRequest;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.controller.dto.UserDTO;
import com.saffrongardens.saffron.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planner")
public class PlannerController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PlannerController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreatePlannerRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body(java.util.Map.of("error", "Username already exists"));
        }
        User user = new User(req.getUsername(), passwordEncoder.encode(req.getPassword()), "EVENT_PLANNER");
        user.setApproved(false);
        user = userRepository.save(user);
        UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.getRole(), user.isApproved());
        return ResponseEntity.status(201).body(dto);
    }
}
