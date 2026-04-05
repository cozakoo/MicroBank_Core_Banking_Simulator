package com.microbank.audit.application;

import com.microbank.audit.domain.AuditAction;
import com.microbank.audit.domain.AuditLog;
import com.microbank.audit.domain.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Registra una acción en el log de auditoría.
     *
     * @param accountId ID de la cuenta afectada
     * @param action Tipo de acción realizada
     * @param details Detalles adicionales de la acción
     * @param userId ID del usuario que realizó la acción (opcional)
     * @return AuditLog creado
     */
    @Transactional
    public AuditLog logAction(UUID accountId, AuditAction action, String details, String userId) {
        log.debug("Registrando auditoría. Account: {}, Action: {}, User: {}",
                accountId, action, userId);

        AuditLog auditLog = new AuditLog(accountId, action, details, userId);
        AuditLog savedLog = auditLogRepository.save(auditLog);

        log.info("Auditoría registrada. ID: {}, Account: {}, Action: {}",
                savedLog.getId(), accountId, action);

        return savedLog;
    }
}
