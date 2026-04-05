package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import com.microbank.account.domain.Transaction;
import com.microbank.account.domain.TransactionRepository;
import com.microbank.account.domain.TransactionStatus;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InsufficientFundsException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account sourceAccount;
    private Account targetAccount;
    private UUID sourceId;
    private UUID targetId;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Crear cuentas de prueba
        sourceAccount = new Account("ACC1111111111111", AccountType.CORRIENTE, new BigDecimal("5000.00"));
        targetAccount = new Account("ACC2222222222222", AccountType.AHORRO, new BigDecimal("1000.00"));

        sourceAccount = accountRepository.save(sourceAccount);
        targetAccount = accountRepository.save(targetAccount);

        sourceId = sourceAccount.getId();
        targetId = targetAccount.getId();
    }

    // ===== TRANSFER FUNDS - SUCCESS TESTS =====

    @Test
    void transferFunds_Success() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("500.00");
        TransferRequest request = new TransferRequest(sourceId, targetId, transferAmount);

        BigDecimal expectedSourceBalance = new BigDecimal("4500.00");
        BigDecimal expectedTargetBalance = new BigDecimal("1500.00");

        // Act
        Transaction result = transferService.transferFunds(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertEquals(transferAmount, result.getAmount());

        // Verificar balances actualizados en BD
        Account updatedSource = accountRepository.findById(sourceId).orElseThrow();
        Account updatedTarget = accountRepository.findById(targetId).orElseThrow();

        assertEquals(expectedSourceBalance, updatedSource.getBalance());
        assertEquals(expectedTargetBalance, updatedTarget.getBalance());
    }

    @Test
    void transferFunds_WithSmallAmount_Success() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("0.01");
        TransferRequest request = new TransferRequest(sourceId, targetId, transferAmount);

        // Act
        Transaction result = transferService.transferFunds(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertEquals(transferAmount, result.getAmount());

        Account updatedSource = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(new BigDecimal("4999.99"), updatedSource.getBalance());
    }

    @Test
    void transferFunds_WithFullBalance_Success() {
        // Arrange
        BigDecimal fullBalance = sourceAccount.getBalance();
        TransferRequest request = new TransferRequest(sourceId, targetId, fullBalance);

        // Act
        Transaction result = transferService.transferFunds(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());

        Account updatedSource = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(0, updatedSource.getBalance().compareTo(BigDecimal.ZERO));
    }

    // ===== TRANSFER FUNDS - INSUFFICIENT FUNDS TESTS =====

    @Test
    void transferFunds_InsufficientFunds_ThrowsException() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("10000.00");
        TransferRequest request = new TransferRequest(sourceId, targetId, transferAmount);

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            transferService.transferFunds(request);
        });

        // Verificar que balances no cambiaron
        Account unchangedSource = accountRepository.findById(sourceId).orElseThrow();
        Account unchangedTarget = accountRepository.findById(targetId).orElseThrow();

        assertEquals(sourceAccount.getBalance(), unchangedSource.getBalance());
        assertEquals(targetAccount.getBalance(), unchangedTarget.getBalance());
    }

    @Test
    void transferFunds_MoreThanBalance_ThrowsException() {
        // Arrange
        BigDecimal transferAmount = sourceAccount.getBalance().add(BigDecimal.ONE);
        TransferRequest request = new TransferRequest(sourceId, targetId, transferAmount);

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    // ===== TRANSFER FUNDS - VALIDATION TESTS =====

    @Test
    void transferFunds_SameAccount_ThrowsException() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("500.00");
        TransferRequest request = new TransferRequest(sourceId, sourceId, transferAmount);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            transferService.transferFunds(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(sourceAccount.getBalance(), unchanged.getBalance());
    }

    @Test
    void transferFunds_ZeroAmount_ThrowsException() {
        // Arrange
        TransferRequest request = new TransferRequest(sourceId, targetId, BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    @Test
    void transferFunds_NegativeAmount_ThrowsException() {
        // Arrange
        TransferRequest request = new TransferRequest(sourceId, targetId, new BigDecimal("-500.00"));

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    @Test
    void transferFunds_NullAmount_ThrowsException() {
        // Arrange
        TransferRequest request = new TransferRequest(sourceId, targetId, null);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    // ===== TRANSFER FUNDS - ACCOUNT NOT FOUND TESTS =====

    @Test
    void transferFunds_SourceAccountNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(nonExistentId, targetId, new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    @Test
    void transferFunds_TargetAccountNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(sourceId, nonExistentId, new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            transferService.transferFunds(request);
        });
    }

    // ===== TRANSACTION RECORD TESTS =====

    @Test
    void transferFunds_CreatesTransactionRecord() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("500.00");
        TransferRequest request = new TransferRequest(sourceId, targetId, transferAmount);

        // Act
        Transaction result = transferService.transferFunds(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(sourceId, result.getSourceAccountId());
        assertEquals(targetId, result.getTargetAccountId());
        assertEquals(transferAmount, result.getAmount());
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertNotNull(result.getCreatedAt());

        // Verificar que la transacción se guardó en BD
        Transaction savedTransaction = transactionRepository.findById(result.getId()).orElseThrow();
        assertEquals(sourceId, savedTransaction.getSourceAccountId());
    }

    @Test
    void transferFunds_MultipleTransfersTracked() {
        // Arrange
        BigDecimal amount1 = new BigDecimal("100.00");
        BigDecimal amount2 = new BigDecimal("200.00");

        TransferRequest request1 = new TransferRequest(sourceId, targetId, amount1);
        TransferRequest request2 = new TransferRequest(targetId, sourceId, amount2);

        // Act
        Transaction transaction1 = transferService.transferFunds(request1);
        Transaction transaction2 = transferService.transferFunds(request2);

        // Assert
        assertNotNull(transaction1);
        assertNotNull(transaction2);
        assertNotEquals(transaction1.getId(), transaction2.getId());

        // Verificar balances finales
        Account finalSource = accountRepository.findById(sourceId).orElseThrow();
        Account finalTarget = accountRepository.findById(targetId).orElseThrow();

        assertEquals(new BigDecimal("5100.00"), finalSource.getBalance());
        assertEquals(new BigDecimal("900.00"), finalTarget.getBalance());
    }
}
