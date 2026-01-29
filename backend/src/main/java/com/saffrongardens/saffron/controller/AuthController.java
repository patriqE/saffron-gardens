package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.LoginRequest;
import com.saffrongardens.saffron.security.JwtUtil;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            String token = jwtUtil.generateToken(req.getUsername());
            // create refresh token
            User u = userRepository.findByUsername(req.getUsername()).orElse(null);
            String refresh = null;
            if (u != null) {
                refresh = refreshTokenService.createRefreshTokenFor(u);
            }
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refresh));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refresh = body.get("refreshToken");
        if (refresh == null) return ResponseEntity.badRequest().body(Map.of("error", "refreshToken required"));
        Optional<User> u = refreshTokenService.validateAndConsumeRefreshToken(refresh);
        if (u.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        String token = jwtUtil.generateToken(u.get().getUsername());
        // rotate: issue a new refresh token
        String newRefresh = refreshTokenService.createRefreshTokenFor(u.get());
        return ResponseEntity.ok(Map.of("token", token, "refreshToken", newRefresh));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        // Accept refreshToken to revoke. If not provided, try to revoke all for authenticated user (client should call /me first)
        String refresh = body.get("refreshToken");
        if (refresh != null) {
            // consume (validate) and revoke if valid
            Optional<User> u = refreshTokenService.validateAndConsumeRefreshToken(refresh);
            u.ifPresent(user -> refreshTokenService.revokeAllForUser(user));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body(Map.of("error", "refreshToken required to logout"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole(),
                "approved", user.isApproved(),
                "canComplete", user.isCanCompleteProfile()
        ));
    }
}
