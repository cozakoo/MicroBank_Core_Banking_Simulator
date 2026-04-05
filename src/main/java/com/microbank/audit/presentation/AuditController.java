package com.microbank.audit.presentation;

import com.microbank.audit.application.AuditService;
import com.microbank.audit.domain.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/audit")
@Validated
@Tag(name = "Audit Management (Admin)", description = "API para consultar registros de auditoría")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    @Autowired
    private AuditService auditService;

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Obtener logs de auditoría por cuenta", description = "Lista todos los registros de auditoría de una cuenta específica ordenados por timestamp descendente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de logs obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<List<AuditLogResponse>> getLogsByAccount(@PathVariable("accountId") UUID accountId) {
        log.debug("Solicitando logs de auditoría para cuenta: {}", accountId);

        List<AuditLog> logs = auditService.getLogsByAccountId(accountId);
        List<AuditLogResponse> responses = logs.stream()
                .map(AuditLogResponse::from)
                .collect(Collectors.toList());

        log.info("Se retornaron {} logs para la cuenta {}", responses.size(), accountId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los logs de auditoría", description = "Lista todos los registros de auditoría del sistema con paginación")
    @ApiResponse(responseCode = "200", description = "Página de logs obtenida exitosamente")
    public ResponseEntity<Page<AuditLogResponse>> getAllLogs(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("Solicitando logs de auditoría. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<AuditLog> logPage = auditService.getAllLogs(pageable);
        Page<AuditLogResponse> responsePage = logPage.map(AuditLogResponse::from);

        log.info("Se retornaron {} logs. Total: {}, Página: {}/{}",
                responsePage.getContent().size(), logPage.getTotalElements(),
                logPage.getNumber() + 1, logPage.getTotalPages());
        return ResponseEntity.ok(responsePage);
    }
}
