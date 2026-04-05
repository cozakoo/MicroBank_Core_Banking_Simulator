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
class DepositWithdrawServiceIntegrationTest {

    @Autowired
    private DepositWithdrawService depositWithdrawService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;
    private UUID accountId;
    private BigDecimal initialBalance;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Crear cuenta de prueba
        initialBalance = new BigDecimal("1000.00");
        account = new Account("ACC1111111111111", AccountType.CORRIENTE, initialBalance);
        account = accountRepository.save(account);
        accountId = account.getId();
    }

    // ===== DEPOSIT TESTS =====

    @Test
    void deposit_Success() {
        // Arrange
        BigDecimal depositAmount = new BigDecimal("500.00");
        DepositRequest request = new DepositRequest(accountId, depositAmount);
        BigDecimal expectedBalance = new BigDecimal("1500.00");

        // Act
        Transaction result = depositWithdrawService.deposit(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertEquals(TransactionType.DEPOSITO, result.getType());
        assertEquals(depositAmount, result.getAmount());
        assertNull(result.getTargetAccountId());

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, updatedAccount.getBalance().compareTo(expectedBalance));
    }

    @Test
    void deposit_AccountNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        DepositRequest request = new DepositRequest(nonExistentId, new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            depositWithdrawService.deposit(request);
        });

        // Verificar que no se creó transaction
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void deposit_InactiveAccount() {
        // Arrange
        account.setStatus(AccountStatus.INACTIVO);
        accountRepository.save(account);
        DepositRequest request = new DepositRequest(accountId, new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(InactiveAccountException.class, () -> {
            depositWithdrawService.deposit(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(accountId).orElseThrow();
        assertEquals(initialBalance, unchanged.getBalance());
    }

    @Test
    void deposit_InvalidAmount() {
        // Arrange
        DepositRequest request = new DepositRequest(accountId, BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            depositWithdrawService.deposit(request);
        });
    }

    @Test
    void deposit_LimitExceeded() {
        // Arrange
        // CORRIENTE tiene límite de 5000
        DepositRequest request = new DepositRequest(accountId, new BigDecimal("6000.00"));

        // Act & Assert
        assertThrows(OperationLimitExceededException.class, () -> {
            depositWithdrawService.deposit(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(accountId).orElseThrow();
        assertEquals(initialBalance, unchanged.getBalance());
    }

    @Test
    void deposit_MaxLimitAllowed() {
        // Arrange
        // CORRIENTE tiene límite de 5000
        BigDecimal maxDeposit = new BigDecimal("5000.00");
        DepositRequest request = new DepositRequest(accountId, maxDeposit);
        BigDecimal expectedBalance = initialBalance.add(maxDeposit);

        // Act
        Transaction result = depositWithdrawService.deposit(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, updatedAccount.getBalance().compareTo(expectedBalance));
    }

    @Test
    void deposit_CreatesTransaction() {
        // Arrange
        BigDecimal depositAmount = new BigDecimal("300.00");
        DepositRequest request = new DepositRequest(accountId, depositAmount);

        // Act
        Transaction result = depositWithdrawService.deposit(request);

        // Assert
        assertNotNull(result.getId());
        assertEquals(accountId, result.getSourceAccountId());
        assertNull(result.getTargetAccountId());
        assertEquals(depositAmount, result.getAmount());
        assertEquals(TransactionType.DEPOSITO, result.getType());
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertNotNull(result.getCreatedAt());

        // Verificar en BD
        Transaction savedTransaction = transactionRepository.findById(result.getId()).orElseThrow();
        assertEquals(TransactionType.DEPOSITO, savedTransaction.getType());
    }

    // ===== WITHDRAWAL TESTS =====

    @Test
    void withdrawal_Success() {
        // Arrange
        BigDecimal withdrawalAmount = new BigDecimal("300.00");
        WithdrawalRequest request = new WithdrawalRequest(accountId, withdrawalAmount);
        BigDecimal expectedBalance = new BigDecimal("700.00");

        // Act
        Transaction result = depositWithdrawService.withdrawal(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertEquals(TransactionType.RETIRO, result.getType());
        assertEquals(withdrawalAmount, result.getAmount());
        assertNull(result.getTargetAccountId());

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, updatedAccount.getBalance().compareTo(expectedBalance));
    }

    @Test
    void withdrawal_AccountNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        WithdrawalRequest request = new WithdrawalRequest(nonExistentId, new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            depositWithdrawService.withdrawal(request);
        });

        // Verificar que no se creó transaction
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void withdrawal_InactiveAccount() {
        // Arrange
        account.setStatus(AccountStatus.SUSPENDIDO);
        accountRepository.save(account);
        WithdrawalRequest request = new WithdrawalRequest(accountId, new BigDecimal("300.00"));

        // Act & Assert
        assertThrows(InactiveAccountException.class, () -> {
            depositWithdrawService.withdrawal(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(accountId).orElseThrow();
        assertEquals(initialBalance, unchanged.getBalance());
    }

    @Test
    void withdrawal_InvalidAmount() {
        // Arrange
        WithdrawalRequest request = new WithdrawalRequest(accountId, new BigDecimal("-500.00"));

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            depositWithdrawService.withdrawal(request);
        });
    }

    @Test
    void withdrawal_InsufficientFunds() {
        // Arrange
        // Balance es 1000, intentamos retirar 2000
        WithdrawalRequest request = new WithdrawalRequest(accountId, new BigDecimal("2000.00"));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            depositWithdrawService.withdrawal(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(accountId).orElseThrow();
        assertEquals(initialBalance, unchanged.getBalance());
    }

    @Test
    void withdrawal_LimitExceeded() {
        // Arrange
        // CORRIENTE tiene límite de 5000, pero balance es 1000
        // Aumentamos balance primero
        account.setBalance(new BigDecimal("6000.00"));
        accountRepository.save(account);

        WithdrawalRequest request = new WithdrawalRequest(accountId, new BigDecimal("6000.00"));

        // Act & Assert
        assertThrows(OperationLimitExceededException.class, () -> {
            depositWithdrawService.withdrawal(request);
        });

        // Verificar que balance no cambió
        Account unchanged = accountRepository.findById(accountId).orElseThrow();
        assertEquals(new BigDecimal("6000.00"), unchanged.getBalance());
    }

    @Test
    void withdrawal_MaxLimitAllowed() {
        // Arrange
        // CORRIENTE tiene límite de 5000, balance es 1000
        // Aumentamos balance a 6000
        account.setBalance(new BigDecimal("6000.00"));
        accountRepository.save(account);

        BigDecimal maxWithdrawal = new BigDecimal("5000.00");
        WithdrawalRequest request = new WithdrawalRequest(accountId, maxWithdrawal);
        BigDecimal expectedBalance = new BigDecimal("1000.00");

        // Act
        Transaction result = depositWithdrawService.withdrawal(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, updatedAccount.getBalance().compareTo(expectedBalance));
    }

    @Test
    void withdrawal_CreatesTransaction() {
        // Arrange
        BigDecimal withdrawalAmount = new BigDecimal("200.00");
        WithdrawalRequest request = new WithdrawalRequest(accountId, withdrawalAmount);

        // Act
        Transaction result = depositWithdrawService.withdrawal(request);

        // Assert
        assertNotNull(result.getId());
        assertEquals(accountId, result.getSourceAccountId());
        assertNull(result.getTargetAccountId());
        assertEquals(withdrawalAmount, result.getAmount());
        assertEquals(TransactionType.RETIRO, result.getType());
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
        assertNotNull(result.getCreatedAt());

        // Verificar en BD
        Transaction savedTransaction = transactionRepository.findById(result.getId()).orElseThrow();
        assertEquals(TransactionType.RETIRO, savedTransaction.getType());
    }

    // ===== DEPOSIT/WITHDRAWAL LIMITS BY ACCOUNT TYPE =====

    @Test
    void deposit_AhorroAccountLimitIs2000() {
        // Arrange
        Account ahorroAccount = new Account("ACC2222222222222", AccountType.AHORRO, initialBalance);
        ahorroAccount = accountRepository.save(ahorroAccount);

        // Intentar depositar 3000 (superior al límite de 2000)
        DepositRequest request = new DepositRequest(ahorroAccount.getId(), new BigDecimal("3000.00"));

        // Act & Assert
        assertThrows(OperationLimitExceededException.class, () -> {
            depositWithdrawService.deposit(request);
        });
    }

    @Test
    void deposit_CreditoAccountLimitIs10000() {
        // Arrange
        Account creditoAccount = new Account("ACC3333333333333", AccountType.CREDITO, initialBalance);
        creditoAccount = accountRepository.save(creditoAccount);

        // Intentar depositar 10000 (igual al límite)
        DepositRequest request = new DepositRequest(creditoAccount.getId(), new BigDecimal("10000.00"));

        // Act
        Transaction result = depositWithdrawService.deposit(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETADA, result.getStatus());
    }

    @Test
    void multipleDepositsAndWithdrawals() {
        // Arrange & Act
        BigDecimal deposit1 = new BigDecimal("500.00");
        BigDecimal deposit2 = new BigDecimal("300.00");
        BigDecimal withdrawal1 = new BigDecimal("400.00");

        depositWithdrawService.deposit(new DepositRequest(accountId, deposit1));
        depositWithdrawService.deposit(new DepositRequest(accountId, deposit2));
        depositWithdrawService.withdrawal(new WithdrawalRequest(accountId, withdrawal1));

        // Assert final balance
        // Inicial: 1000 + 500 + 300 - 400 = 1400
        Account finalAccount = accountRepository.findById(accountId).orElseThrow();
        BigDecimal expectedBalance = new BigDecimal("1400.00");
        assertEquals(0, finalAccount.getBalance().compareTo(expectedBalance));

        // Verificar que se crearon 3 transacciones
        long transactionCount = transactionRepository.findBySourceAccountId(accountId).size();
        assertEquals(3, transactionCount);
    }
}
