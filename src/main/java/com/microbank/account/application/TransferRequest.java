package com.microbank.account.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "TransferRequest",
    description = "Solicitud para realizar una transferencia de fondos entre cuentas",
    example = "{\"sourceAccountId\":\"550e8400-e29b-41d4-a716-446655440001\",\"targetAccountId\":\"550e8400-e29b-41d4-a716-446655440002\",\"amount\":500.00}"
)
public class TransferRequest {

    @NotNull(message = "El ID de cuenta origen es obligatorio")
    @Schema(description = "ID único de la cuenta origen (UUID)", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID sourceAccountId;

    @Schema(description = "ID único de la cuenta destino (UUID). Usar este O targetIdentifier.", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID targetAccountId;

    @Schema(description = "Número de cuenta o alias de la cuenta destino (alternativa al UUID)", example = "ACC12345 o mi-cuenta")
    private String targetIdentifier;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Schema(description = "Monto a transferir en dólares (debe ser mayor a cero)", example = "500.00")
    private BigDecimal amount;

    // Constructor por defecto
    public TransferRequest() {
    }

    public TransferRequest(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
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

    public String getTargetIdentifier() { return targetIdentifier; }
    public void setTargetIdentifier(String targetIdentifier) { this.targetIdentifier = targetIdentifier; }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
