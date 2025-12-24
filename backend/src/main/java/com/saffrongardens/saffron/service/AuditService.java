package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.AuditEvent;
import com.saffrongardens.saffron.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AuditEvent record(String username, String action, String details) {
        AuditEvent ev = new AuditEvent();
        ev.setUsername(username);
        ev.setAction(action);
        ev.setDetails(details);
        ev.setCreatedAt(Instant.now());
        return repository.save(ev);
    }
}
