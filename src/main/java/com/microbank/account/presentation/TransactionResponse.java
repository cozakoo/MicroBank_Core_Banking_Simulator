package com.microbank.account.presentation;

import com.microbank.account.domain.Transaction;
import com.microbank.account.domain.TransactionStatus;
import com.microbank.account.domain.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "TransactionResponse",
    description = "Respuesta con los detalles de una transacción bancaria",
    example = "{\"id\":\"660e8400-e29b-41d4-a716-446655440000\",\"sourceAccountId\":\"550e8400-e29b-41d4-a716-446655440001\",\"targetAccountId\":\"550e8400-e29b-41d4-a716-446655440002\",\"amount\":500.00,\"type\":\"TRANSFERENCIA\",\"status\":\"COMPLETADA\",\"description\":\"Transferencia exitosa\",\"createdAt\":\"2026-04-05T12:30:00\"}"
)
public class TransactionResponse {

    @Schema(description = "ID único de la transacción (UUID)", example = "660e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID de la cuenta origen", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID sourceAccountId;

    @Schema(description = "ID de la cuenta destino (null para depósitos/retiros)", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID targetAccountId;

    @Schema(description = "Monto de la transacción en dólares", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "Tipo de transacción (TRANSFERENCIA, DEPOSITO, RETIRO)", example = "TRANSFERENCIA")
    private TransactionType type;

    @Schema(description = "Estado de la transacción (COMPLETADA, PENDIENTE, FALLIDA)", example = "COMPLETADA")
    private TransactionStatus status;

    @Schema(description = "Descripción detallada de la transacción", example = "Transferencia exitosa")
    private String description;

    @Schema(description = "Fecha y hora de creación de la transacción", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(UUID id, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount,
                               TransactionType type, TransactionStatus status, String description, LocalDateTime createdAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(UUID targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
