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

import java.util.UUID;

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
}
