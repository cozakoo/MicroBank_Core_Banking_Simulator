package com.microbank.account.presentation;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "AccountResponse",
    description = "Respuesta con los detalles completos de una cuenta bancaria",
    example = "{\"id\":\"550e8400-e29b-41d4-a716-446655440000\",\"accountNumber\":\"ACC1111111111111\",\"accountType\":\"CORRIENTE\",\"balance\":5000.00,\"status\":\"ACTIVO\",\"createdAt\":\"2026-04-05T12:30:00\",\"updatedAt\":\"2026-04-05T12:30:00\"}"
)
public class AccountResponse {

    @Schema(description = "Identificador único de la cuenta (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Número de cuenta único (formato: ACC + 15 dígitos)", example = "ACC1111111111111")
    private String accountNumber;

    @Schema(description = "Tipo de cuenta (AHORRO, CORRIENTE, CREDITO)", example = "CORRIENTE")
    private AccountType accountType;

    @Schema(description = "Balance actual de la cuenta en dólares", example = "5000.00")
    private BigDecimal balance;

    @Schema(description = "Estado actual de la cuenta (ACTIVO, INACTIVO)", example = "ACTIVO")
    private AccountStatus status;

    @Schema(description = "Fecha y hora de creación de la cuenta", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de la última actualización", example = "2026-04-05T12:30:00")
    private LocalDateTime updatedAt;

    public AccountResponse() {
    }

    public AccountResponse(UUID id, String accountNumber, AccountType accountType, BigDecimal balance,
                          AccountStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AccountResponse{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
