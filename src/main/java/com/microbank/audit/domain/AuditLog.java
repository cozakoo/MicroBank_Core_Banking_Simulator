package com.microbank.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "auditoria_logs",
        indexes = {
                @Index(name = "idx_audit_account", columnList = "account_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "El ID de cuenta es obligatorio")
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @NotNull(message = "La acción es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "user_id", length = 50)
    private String userId;

    @NotNull(message = "El timestamp es obligatorio")
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // CONSTRUCTORES

    protected AuditLog() {
    }

    public AuditLog(UUID accountId, AuditAction action, String details, String userId) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    // GETTERS Y SETTERS

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

    // EQUALS Y HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // TOSTRING

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", action=" + action +
                ", details='" + details + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
