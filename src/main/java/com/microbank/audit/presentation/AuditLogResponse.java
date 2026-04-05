package com.microbank.audit.presentation;

import com.microbank.audit.domain.AuditAction;
import com.microbank.audit.domain.AuditLog;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "AuditLogResponse",
    description = "Respuesta con los detalles de un registro de auditoría",
    example = "{\"id\":\"770e8400-e29b-41d4-a716-446655440000\",\"accountId\":\"550e8400-e29b-41d4-a716-446655440001\",\"action\":\"TRANSFERENCIA\",\"details\":\"Transferencia de 500.00 desde ACC1111111111111 hacia ACC2222222222222\",\"userId\":null,\"timestamp\":\"2026-04-05T12:30:00\"}"
)
public class AuditLogResponse {

    @Schema(description = "ID único del registro de auditoría (UUID)", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID de la cuenta involucrada en la acción auditada", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID accountId;

    @Schema(description = "Acción auditada (CREACION_CUENTA, DEPOSITO, RETIRO, TRANSFERENCIA, CAMBIO_ESTADO, etc.)", example = "TRANSFERENCIA")
    private AuditAction action;

    @Schema(description = "Detalles adicionales sobre la acción auditada", example = "Transferencia de 500.00 desde ACC1111111111111 hacia ACC2222222222222")
    private String details;

    @Schema(description = "ID del usuario que ejecutó la acción (nullable)", example = "null")
    private String userId;

    @Schema(description = "Fecha y hora exacta de la acción auditada", example = "2026-04-05T12:30:00")
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
