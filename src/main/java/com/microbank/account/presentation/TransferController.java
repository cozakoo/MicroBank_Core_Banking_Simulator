package com.microbank.account.presentation;

import com.microbank.account.application.TransferRequest;
import com.microbank.account.application.TransferService;
import com.microbank.account.domain.Transaction;
import com.microbank.account.domain.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/transfers")
@Validated
@Tag(name = "Transfer Management", description = "API para gestión de transferencias bancarias")
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping
    @Operation(summary = "Realizar una transferencia", description = "Ejecuta una transferencia de fondos entre dos cuentas con atomicidad ACID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transferencia realizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (monto inválido, fondos insuficientes, misma cuenta)"),
            @ApiResponse(responseCode = "404", description = "Una o ambas cuentas no encontradas"),
            @ApiResponse(responseCode = "409", description = "Conflicto de concurrencia (cuenta bloqueada)")
    })
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("Solicitud de transferencia: {} -> {} amount {}", request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount());

        Transaction transaction = transferService.transferFunds(request);
        TransactionResponse response = TransactionResponse.from(transaction);

        log.info("Transferencia ejecutada exitosamente. TransactionID: {}", transaction.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de una transferencia", description = "Obtiene los detalles completos de una transacción por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia encontrada"),
            @ApiResponse(responseCode = "404", description = "Transferencia no encontrada")
    })
    public ResponseEntity<TransactionResponse> getTransfer(@PathVariable("id") UUID id) {
        log.debug("Solicitando transferencia con ID: {}", id);

        try {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new com.microbank.shared.exceptions.AccountNotFoundException("Transferencia no encontrada con ID: " + id));

            TransactionResponse response = TransactionResponse.from(transaction);
            log.info("Transferencia obtenida. ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (com.microbank.shared.exceptions.AccountNotFoundException e) {
            log.warn("Transferencia no encontrada: {}", id);
            throw e;
        }
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Obtener transferencias de una cuenta", description = "Lista todas las transferencias (entrada y salida) asociadas a una cuenta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de transferencias obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable("accountId") UUID accountId) {
        log.debug("Solicitando transferencias para cuenta: {}", accountId);

        List<Transaction> outgoing = transactionRepository.findBySourceAccountId(accountId);
        List<Transaction> incoming = transactionRepository.findByTargetAccountId(accountId);

        List<TransactionResponse> responses = Stream.concat(outgoing.stream(), incoming.stream())
                .map(TransactionResponse::from)
                .collect(Collectors.toList());

        log.info("Se retornaron {} transferencias para la cuenta {}", responses.size(), accountId);
        return ResponseEntity.ok(responses);
    }
}
