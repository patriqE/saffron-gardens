package com.saffrongardens.saffron.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long validityMillis;

    public JwtUtil(@Value("${app.jwt.secret:change_this_default_secret}") String secret,
                   @Value("${app.jwt.ttl-ms:3600000}") long ttlMs) {
        // Use provided secret bytes as HMAC key when secure enough; otherwise generate a secure random key.
        Key key;
        try {
            key = Keys.hmacShaKeyFor(secret.getBytes());
        } catch (io.jsonwebtoken.security.WeakKeyException ex) {
            // fallback to a secure random key for HS256
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        this.signingKey = key;
        this.validityMillis = ttlMs;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMillis);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
