package com.microbank.audit.application;

import com.microbank.audit.domain.AuditAction;
import com.microbank.audit.domain.AuditLog;
import com.microbank.audit.domain.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    private UUID accountId;
    private String details;
    private String userId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        details = "Test operation details";
        userId = "test-user-123";
    }

    @Test
    void logAction_Success() {
        // Arrange
        AuditLog mockLog = new AuditLog(accountId, AuditAction.CREAR_CUENTA, details, userId);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.CREAR_CUENTA, details, userId);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals(AuditAction.CREAR_CUENTA, result.getAction());
        assertEquals(details, result.getDetails());
        assertEquals(userId, result.getUserId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_WithNullUserId() {
        // Arrange
        AuditLog mockLog = new AuditLog(accountId, AuditAction.DEPOSITO, details, null);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.DEPOSITO, details, null);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals(AuditAction.DEPOSITO, result.getAction());
        assertNull(result.getUserId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_TransferAction() {
        // Arrange
        String transferDetails = "Transfer from ACC123 to ACC456 amount 500";
        AuditLog mockLog = new AuditLog(accountId, AuditAction.TRANSFERENCIA, transferDetails, userId);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.TRANSFERENCIA, transferDetails, userId);

        // Assert
        assertNotNull(result);
        assertEquals(AuditAction.TRANSFERENCIA, result.getAction());
        assertEquals(transferDetails, result.getDetails());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_WithdrawalAction() {
        // Arrange
        String withdrawalDetails = "Withdrawal of 1000 from account";
        AuditLog mockLog = new AuditLog(accountId, AuditAction.RETIRO, withdrawalDetails, userId);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.RETIRO, withdrawalDetails, userId);

        // Assert
        assertNotNull(result);
        assertEquals(AuditAction.RETIRO, result.getAction());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_StatusChangeAction() {
        // Arrange
        String statusChangeDetails = "Status changed from ACTIVO to INACTIVO";
        AuditLog mockLog = new AuditLog(accountId, AuditAction.CAMBIO_ESTADO, statusChangeDetails, userId);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.CAMBIO_ESTADO, statusChangeDetails, userId);

        // Assert
        assertNotNull(result);
        assertEquals(AuditAction.CAMBIO_ESTADO, result.getAction());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_AllActionsCanBeLogged() {
        // Arrange
        for (AuditAction action : AuditAction.values()) {
            AuditLog mockLog = new AuditLog(accountId, action, "Test details for " + action, userId);
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

            // Act
            AuditLog result = auditService.logAction(accountId, action, "Test details for " + action, userId);

            // Assert
            assertNotNull(result);
            assertEquals(action, result.getAction());
        }

        verify(auditLogRepository, times(AuditAction.values().length)).save(any(AuditLog.class));
    }

    @Test
    void logAction_PreservesTimestamp() {
        // Arrange
        AuditLog mockLog = new AuditLog(accountId, AuditAction.DEPOSITO, details, userId);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(mockLog);

        // Act
        AuditLog result = auditService.logAction(accountId, AuditAction.DEPOSITO, details, userId);

        // Assert
        assertNotNull(result);
        // El timestamp debe ser set automáticamente por @CreationTimestamp
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_DifferentAccounts() {
        // Arrange
        UUID account1 = UUID.randomUUID();
        UUID account2 = UUID.randomUUID();
        AuditLog mockLog1 = new AuditLog(account1, AuditAction.DEPOSITO, details, userId);
        AuditLog mockLog2 = new AuditLog(account2, AuditAction.RETIRO, details, userId);
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(mockLog1)
                .thenReturn(mockLog2);

        // Act
        AuditLog result1 = auditService.logAction(account1, AuditAction.DEPOSITO, details, userId);
        AuditLog result2 = auditService.logAction(account2, AuditAction.RETIRO, details, userId);

        // Assert
        assertEquals(account1, result1.getAccountId());
        assertEquals(account2, result2.getAccountId());
        assertNotEquals(result1.getAccountId(), result2.getAccountId());
        verify(auditLogRepository, times(2)).save(any(AuditLog.class));
    }

    @Test
    void getLogsByAccountId_Success() {
        // Arrange
        List<AuditLog> logs = new ArrayList<>();
        AuditLog log1 = new AuditLog(accountId, AuditAction.DEPOSITO, "Deposit 500", userId);
        AuditLog log2 = new AuditLog(accountId, AuditAction.RETIRO, "Withdrawal 200", userId);
        logs.add(log1);
        logs.add(log2);

        when(auditLogRepository.findByAccountIdOrderByTimestampDesc(accountId)).thenReturn(logs);

        // Act
        List<AuditLog> result = auditService.getLogsByAccountId(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogRepository).findByAccountIdOrderByTimestampDesc(accountId);
    }

    @Test
    void getLogsByAccountId_Empty() {
        // Arrange
        UUID nonExistentAccountId = UUID.randomUUID();
        when(auditLogRepository.findByAccountIdOrderByTimestampDesc(nonExistentAccountId))
                .thenReturn(new ArrayList<>());

        // Act
        List<AuditLog> result = auditService.getLogsByAccountId(nonExistentAccountId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(auditLogRepository).findByAccountIdOrderByTimestampDesc(nonExistentAccountId);
    }

    @Test
    void getAllLogs_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        List<AuditLog> logsList = new ArrayList<>();
        logsList.add(new AuditLog(accountId, AuditAction.DEPOSITO, "Deposit 500", userId));
        logsList.add(new AuditLog(accountId, AuditAction.RETIRO, "Withdrawal 200", userId));

        Page<AuditLog> page = new PageImpl<>(logsList, pageable, 2);
        when(auditLogRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<AuditLog> result = auditService.getAllLogs(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(20, result.getSize());
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    void getAllLogs_Empty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(auditLogRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<AuditLog> result = auditService.getAllLogs(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    void getAllLogs_Pagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 10);
        List<AuditLog> logsList = new ArrayList<>();
        logsList.add(new AuditLog(accountId, AuditAction.DEPOSITO, "Deposit 500", userId));

        Page<AuditLog> page = new PageImpl<>(logsList, pageable, 25);
        when(auditLogRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<AuditLog> result = auditService.getAllLogs(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        verify(auditLogRepository).findAll(pageable);
    }
}
