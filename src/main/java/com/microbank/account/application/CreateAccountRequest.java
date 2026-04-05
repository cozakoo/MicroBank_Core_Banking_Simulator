package com.microbank.account.application;

import com.microbank.account.domain.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateAccountRequest {

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @NotNull(message = "El balance inicial es obligatorio")
    @Positive(message = "El balance debe ser mayor a cero")
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
