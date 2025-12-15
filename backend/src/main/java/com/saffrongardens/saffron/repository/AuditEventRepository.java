package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}
