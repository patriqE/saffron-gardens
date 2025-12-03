package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.LoginRequest;
import com.saffrongardens.saffron.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.path:/}")
    private String cookiePath;

    @Value("${app.cookie.samesite:Lax}")
    private String cookieSameSite;

    @Value("${app.cookie.max-age-seconds:2592000}")
    private long cookieMaxAgeSeconds;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

                String accessToken = jwtUtil.generateToken(req.getUsername());
                String refreshToken = jwtUtil.generateRefreshToken(req.getUsername());

                // determine role from authentication
                String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("");
                if (role.startsWith("ROLE_")) role = role.substring(5);

                // set refresh token as HttpOnly cookie
                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .path(cookiePath)
                    .maxAge(cookieMaxAgeSeconds)
                    .sameSite(cookieSameSite)
                    .secure(cookieSecure)
                    .build();

                return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", accessToken, "user", Map.of("username", req.getUsername(), "role", role)));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // read refresh token from cookie
        String refresh = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    refresh = c.getValue();
                    break;
                }
            }
        }
        if (refresh == null) return ResponseEntity.status(401).body(Map.of("error","No refresh token"));
        try {
            var claims = jwtUtil.validateAndGetClaims(refresh);
            String username = claims.getSubject();
            String accessToken = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("accessToken", accessToken));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error","Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Lax")
                .secure(false)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Map.of("ok", true));
    }
}
