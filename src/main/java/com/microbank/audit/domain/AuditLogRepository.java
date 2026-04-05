package com.microbank.audit.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByAccountId(UUID accountId);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByAccountIdAndActionOrderByTimestampDesc(UUID accountId, AuditAction action);

    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByAccountIdOrderByTimestampDesc(UUID accountId);
}
