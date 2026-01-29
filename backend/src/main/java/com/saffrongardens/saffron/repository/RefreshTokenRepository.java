package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.RefreshToken;
import com.saffrongardens.saffron.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUser(User user);
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);
}
