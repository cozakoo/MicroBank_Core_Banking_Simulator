package com.microbank.account.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "WithdrawalRequest",
    description = "Solicitud para realizar un retiro de una cuenta",
    example = "{\"accountId\":\"550e8400-e29b-41d4-a716-446655440001\",\"amount\":500.00}"
)
public class WithdrawalRequest {

    @NotNull(message = "El ID de cuenta es obligatorio")
    @Schema(description = "ID único de la cuenta desde donde se hará el retiro (UUID)", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID accountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Schema(description = "Monto a retirar en dólares (debe ser mayor a cero, no puede exceder balance disponible)", example = "500.00")
    private BigDecimal amount;

    // Constructor por defecto
    public WithdrawalRequest() {
    }

    public WithdrawalRequest(UUID accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
