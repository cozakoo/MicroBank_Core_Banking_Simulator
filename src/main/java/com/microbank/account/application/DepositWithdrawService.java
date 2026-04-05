package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import com.microbank.account.domain.Transaction;
import com.microbank.account.domain.TransactionRepository;
import com.microbank.account.domain.TransactionStatus;
import com.microbank.account.domain.TransactionType;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InactiveAccountException;
import com.microbank.shared.exceptions.InsufficientFundsException;
import com.microbank.shared.exceptions.InvalidAccountException;
import com.microbank.shared.exceptions.OperationLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DepositWithdrawService {

    private static final Logger log = LoggerFactory.getLogger(DepositWithdrawService.class);

    // Límites de operación por tipo de cuenta
    private static final Map<AccountType, BigDecimal> OPERATION_LIMITS = new HashMap<>();

    static {
        OPERATION_LIMITS.put(AccountType.AHORRO, new BigDecimal("2000.00"));
        OPERATION_LIMITS.put(AccountType.CORRIENTE, new BigDecimal("5000.00"));
        OPERATION_LIMITS.put(AccountType.CREDITO, new BigDecimal("10000.00"));
    }

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Realiza un depósito a una cuenta.
     *
     * @param request DTO con ID de cuenta y monto
     * @return Transaction con status COMPLETED
     * @throws AccountNotFoundException si la cuenta no existe
     * @throws InactiveAccountException si la cuenta no está activa
     * @throws InvalidAccountException si el monto es inválido
     * @throws OperationLimitExceededException si excede el límite de operación
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction deposit(DepositRequest request) {
        UUID accountId = request.getAccountId();
        BigDecimal amount = request.getAmount();

        log.info("Iniciando depósito. Account ID: {}, Amount: {}", accountId, amount);

        // Validar monto > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Monto inválido para depósito: {}", amount);
            throw new InvalidAccountException("El monto debe ser mayor a cero");
        }

        // Obtener cuenta
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> {
                    log.error("Cuenta no encontrada. ID: {}", accountId);
                    return new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId);
                });

        // Validar que la cuenta está activa
        if (!account.getStatus().equals(AccountStatus.ACTIVO)) {
            log.warn("Intento de depósito en cuenta inactiva. Account: {}, Status: {}",
                    account.getAccountNumber(), account.getStatus());
            throw new InactiveAccountException("La cuenta no está activa. Estado: " + account.getStatus());
        }

        // Validar límite de operación
        BigDecimal limit = OPERATION_LIMITS.get(account.getAccountType());
        if (amount.compareTo(limit) > 0) {
            log.warn("Límite de operación excedido. Account: {}, Amount: {}, Limit: {}",
                    account.getAccountNumber(), amount, limit);
            throw new OperationLimitExceededException(
                    String.format("Límite de depósito excedido. Máximo permitido: %s, Solicitado: %s", limit, amount)
            );
        }

        // Realizar depósito
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        log.debug("Balance actualizado. Account: {}, Nuevo balance: {}", account.getAccountNumber(), newBalance);

        // Guardar cambios
        accountRepository.save(account);

        // Crear registro de transacción
        Transaction transaction = new Transaction(
                account.getId(),
                null,  // No hay cuenta destino en depósito
                amount,
                TransactionType.DEPOSITO,
                TransactionStatus.COMPLETADA,
                "Depósito exitoso",
                LocalDateTime.now()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Depósito completado. Account: {}, Amount: {}, TransactionID: {}",
                account.getAccountNumber(), amount, savedTransaction.getId());

        return savedTransaction;
    }

    /**
     * Realiza un retiro de una cuenta.
     *
     * @param request DTO con ID de cuenta y monto
     * @return Transaction con status COMPLETED
     * @throws AccountNotFoundException si la cuenta no existe
     * @throws InactiveAccountException si la cuenta no está activa
     * @throws InvalidAccountException si el monto es inválido
     * @throws InsufficientFundsException si no hay balance suficiente
     * @throws OperationLimitExceededException si excede el límite de operación
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction withdrawal(WithdrawalRequest request) {
        UUID accountId = request.getAccountId();
        BigDecimal amount = request.getAmount();

        log.info("Iniciando retiro. Account ID: {}, Amount: {}", accountId, amount);

        // Validar monto > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Monto inválido para retiro: {}", amount);
            throw new InvalidAccountException("El monto debe ser mayor a cero");
        }

        // Obtener cuenta
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> {
                    log.error("Cuenta no encontrada. ID: {}", accountId);
                    return new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId);
                });

        // Validar que la cuenta está activa
        if (!account.getStatus().equals(AccountStatus.ACTIVO)) {
            log.warn("Intento de retiro en cuenta inactiva. Account: {}, Status: {}",
                    account.getAccountNumber(), account.getStatus());
            throw new InactiveAccountException("La cuenta no está activa. Estado: " + account.getStatus());
        }

        // Validar límite de operación
        BigDecimal limit = OPERATION_LIMITS.get(account.getAccountType());
        if (amount.compareTo(limit) > 0) {
            log.warn("Límite de operación excedido. Account: {}, Amount: {}, Limit: {}",
                    account.getAccountNumber(), amount, limit);
            throw new OperationLimitExceededException(
                    String.format("Límite de retiro excedido. Máximo permitido: %s, Solicitado: %s", limit, amount)
            );
        }

        // Validar balance suficiente
        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Balance insuficiente. Account: {}, Balance: {}, Amount: {}",
                    account.getAccountNumber(), account.getBalance(), amount);
            throw new InsufficientFundsException(
                    String.format("Balance insuficiente. Disponible: %s, Solicitado: %s",
                            account.getBalance(), amount)
            );
        }

        // Realizar retiro
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        log.debug("Balance actualizado. Account: {}, Nuevo balance: {}", account.getAccountNumber(), newBalance);

        // Guardar cambios
        accountRepository.save(account);

        // Crear registro de transacción
        Transaction transaction = new Transaction(
                account.getId(),
                null,  // No hay cuenta destino en retiro
                amount,
                TransactionType.RETIRO,
                TransactionStatus.COMPLETADA,
                "Retiro exitoso",
                LocalDateTime.now()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Retiro completado. Account: {}, Amount: {}, TransactionID: {}",
                account.getAccountNumber(), amount, savedTransaction.getId());

        return savedTransaction;
    }
}
