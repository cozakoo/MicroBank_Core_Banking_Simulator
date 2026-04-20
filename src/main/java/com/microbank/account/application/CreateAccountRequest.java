package com.microbank.account.application;

import com.microbank.account.domain.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Schema(
    name = "CreateAccountRequest",
    description = "Solicitud para crear una nueva cuenta bancaria",
    example = "{\"accountType\":\"CORRIENTE\",\"initialBalance\":5000.00}"
)
public class CreateAccountRequest {

    @NotNull(message = "El tipo de cuenta es obligatorio")
    @Schema(description = "Tipo de cuenta a crear (AHORRO, CORRIENTE, CREDITO)", example = "CORRIENTE")
    private AccountType accountType;

    @NotNull(message = "El balance inicial es obligatorio")
    @Positive(message = "El balance debe ser mayor a cero")
    @Digits(integer = 12, fraction = 2, message = "El balance supera el límite máximo permitido (12 dígitos)")
    @Schema(description = "Balance inicial de la cuenta en dólares (debe ser mayor a cero)", example = "5000.00")
    private BigDecimal initialBalance;

    // Constructor por defecto (para deserialización JSON)
    public CreateAccountRequest() {
    }

    public CreateAccountRequest(AccountType accountType, BigDecimal initialBalance) {
        this.accountType = accountType;
        this.initialBalance = initialBalance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
