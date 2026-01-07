package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    Optional<RegistrationRequest> findByUsername(String username);
    List<RegistrationRequest> findByStatus(String status);
}

