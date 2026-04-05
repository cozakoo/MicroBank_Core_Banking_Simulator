package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountRequest validRequest;
    private Account mockAccount;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        validRequest = new CreateAccountRequest(AccountType.AHORRO, new BigDecimal("1000.00"));

        mockAccount = new Account("ACC123456789012345", AccountType.AHORRO, new BigDecimal("1000.00"));
    }

    // ===== CREATE ACCOUNT TESTS =====

    @Test
    void createAccount_Success() {
        // Arrange
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // Act
        Account result = accountService.createAccount(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(AccountType.AHORRO, result.getAccountType());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(AccountStatus.ACTIVO, result.getStatus());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.CORRIENTE, BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithNullBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.CREDITO, null);

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithNegativeBalance_ThrowsException() {
        // Arrange
        CreateAccountRequest invalidRequest = new CreateAccountRequest(AccountType.AHORRO, new BigDecimal("-500.00"));

        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.createAccount(invalidRequest);
        });
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_GeneratesUniqueAccountNumber() {
        // Arrange
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // Act
        accountService.createAccount(validRequest);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertNotNull(savedAccount.getAccountNumber());
        assertTrue(savedAccount.getAccountNumber().startsWith("ACC"));
    }

    // ===== GET ACCOUNT BY ID TESTS =====

    @Test
    void getAccountById_Success() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // Act
        Account result = accountService.getAccountById(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(mockAccount.getAccountNumber(), result.getAccountNumber());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountById_NotFound_ThrowsException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(accountId);
        });
        verify(accountRepository, times(1)).findById(accountId);
    }

    // ===== GET ACCOUNT BY NUMBER TESTS =====

    @Test
    void getAccountByNumber_Success() {
        // Arrange
        String accountNumber = "ACC123456789012345";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(mockAccount));

        // Act
        Account result = accountService.getAccountByNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByNumber_NotFound_ThrowsException() {
        // Arrange
        String accountNumber = "ACC999999999999999";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByNumber(accountNumber);
        });
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    // ===== GET ALL ACCOUNTS TESTS =====

    @Test
    void getAllAccounts_Success() {
        // Arrange
        Account account1 = new Account("ACC111111111111111", AccountType.AHORRO, new BigDecimal("1000.00"));
        Account account2 = new Account("ACC222222222222222", AccountType.CORRIENTE, new BigDecimal("2000.00"));
        List<Account> accounts = Arrays.asList(account1, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void getAllAccounts_EmptyList() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository, times(1)).findAll();
    }

    // ===== UPDATE ACCOUNT STATUS TESTS =====

    @Test
    void updateAccountStatus_Success() {
        // Arrange
        Account accountToUpdate = new Account("ACC123456789012345", AccountType.AHORRO, new BigDecimal("1000.00"));
        accountToUpdate = new Account("ACC123456789012345", AccountType.AHORRO, new BigDecimal("1000.00"));
        AccountStatus newStatus = AccountStatus.INACTIVO;

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountToUpdate));
        when(accountRepository.save(any(Account.class))).thenReturn(accountToUpdate);

        // Act
        Account result = accountService.updateAccountStatus(accountId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccountStatus_AccountNotFound_ThrowsException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.updateAccountStatus(accountId, AccountStatus.INACTIVO);
        });
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccountStatus_WithNullStatus_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidAccountException.class, () -> {
            accountService.updateAccountStatus(accountId, null);
        });
        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
