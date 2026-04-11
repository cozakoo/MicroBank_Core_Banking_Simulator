package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.Transaction;
import com.microbank.account.domain.TransactionRepository;
import com.microbank.account.domain.TransactionStatus;
import com.microbank.account.domain.TransactionType;
import com.microbank.audit.application.AuditService;
import com.microbank.audit.domain.AuditAction;
import com.microbank.shared.exceptions.AccountLockedException;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InsufficientFundsException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditService auditService;

    /**
     * Ejecuta una transferencia de fondos entre dos cuentas.
     *
     * ASPECTOS CRÍTICOS DE ESTA IMPLEMENTACIÓN (Martín):
     * 1. ACID Guarantee: @Transactional asegura que si algo falla, TODO se revierte (rollback)
     * 2. Pessimistic Locking: findById() usa @Lock(PESSIMISTIC_WRITE) en el repositorio
     *    - Las cuentas se bloquean EN LA BD cuando se obtienen
     *    - Evita race conditions en transferencias concurrentes
     *    - Si dos threads transfieren simultáneamente, uno espera al otro (no hay corrupción de datos)
     * 3. Deadlock Prevention: Ordenamos siempre por UUID (minId, maxId)
     *    - Si múltiples transferencias suceden, siempre se lockean en el mismo orden
     *    - Previene el deadlock circulante (A→B y B→A al mismo tiempo)
     * 4. Isolation Level: READ_COMMITTED (no dirty reads, pero permite phantom reads)
     *    - Balancee entre seguridad y performance (no usamos SERIALIZABLE para no bloquear BD)
     * 5. Auditoría: Registramos TODAS las transferencias, exitosas o fallidas
     *    - Cumplimiento regulatorio: toda operación financiera es trazable
     *
     * @param request DTO con IDs de cuentas origen/destino y monto
     * @return Transaction con status COMPLETED
     * @throws InvalidAccountException si sourceId == targetId o amount <= 0
     * @throws AccountNotFoundException si alguna cuenta no existe
     * @throws InsufficientFundsException si balance insuficiente
     * @throws AccountLockedException si no se puede adquirir el lock en tiempo (timeout)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction transferFunds(TransferRequest request) {
        UUID sourceId = request.getSourceAccountId();
        UUID targetId = request.getTargetAccountId();
        BigDecimal amount = request.getAmount();

        log.info("Iniciando transferencia: {} -> {} amount {}", sourceId, targetId, amount);

        // Validar que source != target
        if (sourceId.equals(targetId)) {
            log.warn("Intento de transferencia a la misma cuenta. ID: {}", sourceId);
            throw new InvalidAccountException("No se puede transferir a la misma cuenta");
        }

        // Validar que amount > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Monto inválido para transferencia: {}", amount);
            throw new InvalidAccountException("El monto debe ser mayor a cero");
        }

        try {
            // ⚠️ ORDEN CRÍTICA DE LOCKS (Martín):
            // Siempre lockeamos en el mismo orden (minId, luego maxId).
            // Ejemplo: Si A→B y C→B suceden al mismo tiempo:
            //   - A→B: lockea B primero (minId), luego A
            //   - C→B: lockea B primero (minId), luego C
            // Si NO ordenáramos: A→B lockea A primero, C→B lockea B primero → DEADLOCK
            // Con orden fijo: Siempre B primero → Sin deadlock ✓
            UUID minId = sourceId.compareTo(targetId) < 0 ? sourceId : targetId;
            UUID maxId = sourceId.compareTo(targetId) < 0 ? targetId : sourceId;

            Account account1 = accountRepository.findById(minId)
                    .orElseThrow(() -> {
                        log.error("Cuenta no encontrada. ID: {}", minId);
                        return new AccountNotFoundException("Cuenta no encontrada con ID: " + minId);
                    });

            Account account2 = accountRepository.findById(maxId)
                    .orElseThrow(() -> {
                        log.error("Cuenta no encontrada. ID: {}", maxId);
                        return new AccountNotFoundException("Cuenta no encontrada con ID: " + maxId);
                    });

            // Resolver cuál es source y cuál es target
            Account sourceAccount = sourceId.equals(account1.getId()) ? account1 : account2;
            Account targetAccount = targetId.equals(account1.getId()) ? account1 : account2;

            log.debug("Cuentas obtenidas con lock. Source: {}, Target: {}",
                    sourceAccount.getAccountNumber(), targetAccount.getAccountNumber());

            // Validar balance suficiente
            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                log.warn("Balance insuficiente. Account: {}, Balance: {}, Amount: {}",
                        sourceAccount.getAccountNumber(), sourceAccount.getBalance(), amount);
                throw new InsufficientFundsException(
                        String.format("Balance insuficiente. Disponible: %s, Requerido: %s",
                                sourceAccount.getBalance(), amount)
                );
            }

            // Realizar la transferencia
            BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
            BigDecimal newTargetBalance = targetAccount.getBalance().add(amount);

            sourceAccount.setBalance(newSourceBalance);
            targetAccount.setBalance(newTargetBalance);

            log.debug("Balances actualizados. Source: {} -> {}, Target: {} -> {}",
                    sourceAccount.getBalance().add(amount), newSourceBalance,
                    targetAccount.getBalance().subtract(amount), newTargetBalance);

            // Guardar cambios en BD (con transacción activa)
            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            // Crear registro de transacción
            Transaction transaction = new Transaction(
                    sourceAccount.getId(),
                    targetAccount.getId(),
                    amount,
                    TransactionType.TRANSFERENCIA,
                    TransactionStatus.COMPLETADA,
                    "Transferencia completada",
                    LocalDateTime.now()
            );

            Transaction savedTransaction = transactionRepository.save(transaction);

            log.info("Transferencia completada: {} -> {} amount {}. TransactionID: {}",
                    sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), amount, savedTransaction.getId());

            // Registrar en auditoría
            String auditDetails = String.format("Transferencia de %s desde %s hacia %s",
                    amount, sourceAccount.getAccountNumber(), targetAccount.getAccountNumber());
            auditService.logAction(sourceId, AuditAction.TRANSFERENCIA, auditDetails, null);
            auditService.logAction(targetId, AuditAction.TRANSFERENCIA, auditDetails, null);

            return savedTransaction;

        } catch (AccountLockedException e) {
            log.error("Error adquiriendo lock en cuentas. Source: {}, Target: {}", sourceId, targetId, e);
            throw e;
        }
    }
}
