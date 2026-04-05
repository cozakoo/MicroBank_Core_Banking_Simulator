package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private CreateAccountRequest validRequest;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        accountId = UUID.randomUUID();
        validRequest = new CreateAccountRequest(AccountType.AHORRO, new BigDecimal("1000.00"));
    }

    // ===== CREATE ACCOUNT TESTS =====

    @Test
    void createAccount_Success() {
        // Act
        Account result = accountService.createAccount(validRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AccountType.AHORRO, result.getAccountType());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(AccountStatus.ACTIVO, result.getStatus());
    }

    @Test
    void createAccount_WithInvalidBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.CORRIENTE, BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
    }

    @Test
    void createAccount_WithNullBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.CREDITO, null);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
    }

    @Test
    void createAccount_WithNegativeBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.AHORRO, new BigDecimal("-500.00"));

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
    }

    @Test
    void createAccount_GeneratesUniqueAccountNumber() {
        // Act
        Account result1 = accountService.createAccount(validRequest);
        Account result2 = accountService.createAccount(validRequest);

        // Assert
        assertNotNull(result1.getAccountNumber());
        assertNotNull(result2.getAccountNumber());
        assertTrue(result1.getAccountNumber().startsWith("ACC"));
        assertTrue(result2.getAccountNumber().startsWith("ACC"));
        assertNotEquals(result1.getAccountNumber(), result2.getAccountNumber());
    }

    // ===== GET ACCOUNT BY ID TESTS =====

    @Test
    void getAccountById_Success() {
        // Arrange
        Account savedAccount = accountService.createAccount(validRequest);

        // Act
        Account result = accountService.getAccountById(savedAccount.getId());

        // Assert
        assertNotNull(result);
        assertEquals(savedAccount.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void getAccountById_NotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(nonExistentId);
        });
    }

    // ===== GET ACCOUNT BY NUMBER TESTS =====

    @Test
    void getAccountByNumber_Success() {
        // Arrange
        Account savedAccount = accountService.createAccount(validRequest);
        String accountNumber = savedAccount.getAccountNumber();

        // Act
        Account result = accountService.getAccountByNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
    }

    @Test
    void getAccountByNumber_NotFound_ThrowsException() {
        // Arrange
        String nonExistentNumber = "ACC999999999999999";

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByNumber(nonExistentNumber);
        });
    }

    // ===== GET ALL ACCOUNTS TESTS =====

    @Test
    void getAllAccounts_Success() {
        // Arrange
        accountService.createAccount(validRequest);
        accountService.createAccount(new CreateAccountRequest(AccountType.CORRIENTE, new BigDecimal("2000.00")));

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllAccounts_EmptyList() {
        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== UPDATE ACCOUNT STATUS TESTS =====

    @Test
    void updateAccountStatus_Success() {
        // Arrange
        Account savedAccount = accountService.createAccount(validRequest);
        AccountStatus newStatus = AccountStatus.INACTIVO;

        // Act
        Account result = accountService.updateAccountStatus(savedAccount.getId(), newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
    }

    @Test
    void updateAccountStatus_AccountNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.updateAccountStatus(nonExistentId, AccountStatus.INACTIVO);
        });
    }

    @Test
    void updateAccountStatus_WithNullStatus_ThrowsException() {
        // Arrange
        Account savedAccount = accountService.createAccount(validRequest);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.updateAccountStatus(savedAccount.getId(), null);
        });
    }
}
