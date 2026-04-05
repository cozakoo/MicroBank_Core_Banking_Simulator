package com.microbank.audit.presentation;

import com.microbank.audit.domain.AuditAction;
import com.microbank.audit.domain.AuditLog;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLogResponse {

    private UUID id;
    private UUID accountId;
    private AuditAction action;
    private String details;
    private String userId;
    private LocalDateTime timestamp;

    public AuditLogResponse() {
    }

    public AuditLogResponse(UUID id, UUID accountId, AuditAction action, String details, String userId, LocalDateTime timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAccountId(),
                auditLog.getAction(),
                auditLog.getDetails(),
                auditLog.getUserId(),
                auditLog.getTimestamp()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuditLogResponse{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", action=" + action +
                ", details='" + details + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
