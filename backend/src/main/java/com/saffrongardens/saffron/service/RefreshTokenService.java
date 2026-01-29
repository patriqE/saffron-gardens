package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.RefreshToken;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository repo;
    private final SecureRandom random = new SecureRandom();
    private final long refreshTtlSeconds;

    public RefreshTokenService(RefreshTokenRepository repo, @Value("${app.jwt.refresh-ttl-sec:1209600}") long refreshTtlSeconds) {
        this.repo = repo;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    // generate a token string and persist a hash
    public String createRefreshTokenFor(User user) {
        // generate a secure random token
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) + "." + UUID.randomUUID();

        String hash = BCrypt.hashpw(token, BCrypt.gensalt(10));

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hash);
        rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTtlSeconds));
        repo.save(rt);
        return token;
    }

    public Optional<User> validateAndConsumeRefreshToken(String token) {
        // find by comparing BCrypt against stored hashes: fetch all tokens that haven't expired and match
        OffsetDateTime now = OffsetDateTime.now();
        // Fetch all non-expired tokens and compare
        Optional<RefreshToken> found = repo.findAll().stream()
                .filter(rt -> rt.getExpiresAt().isAfter(now))
                .filter(rt -> BCrypt.checkpw(token, rt.getTokenHash()))
                .findFirst();

        if (found.isEmpty()) return Optional.empty();
        User u = found.get().getUser();
        // consume: delete the refresh token so it cannot be reused (rotation)
        repo.delete(found.get());
        return Optional.of(u);
    }

    public void revokeAllForUser(User user) {
        repo.deleteByUser(user);
    }

    public void cleanupExpired() {
        repo.deleteByExpiresAtBefore(OffsetDateTime.now());
    }
}
