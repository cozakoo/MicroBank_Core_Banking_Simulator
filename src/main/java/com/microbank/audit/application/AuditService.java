package com.microbank.audit.application;

import com.microbank.audit.domain.AuditAction;
import com.microbank.audit.domain.AuditLog;
import com.microbank.audit.domain.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * Obtiene todos los logs de auditoría de una cuenta específica.
     *
     * @param accountId ID de la cuenta
     * @return Lista de logs ordenados por timestamp descendente
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAccountId(UUID accountId) {
        log.debug("Obteniendo logs de auditoría para cuenta: {}", accountId);
        return auditLogRepository.findByAccountIdOrderByTimestampDesc(accountId);
    }

    /**
     * Obtiene todos los logs de auditoría con paginación.
     *
     * @param pageable Parámetro de paginación
     * @return Página de logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        log.debug("Obteniendo todos los logs de auditoría. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return auditLogRepository.findAll(pageable);
    }
}
