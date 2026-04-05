package com.microbank.account.presentation;

import com.microbank.account.application.DepositRequest;
import com.microbank.account.application.DepositWithdrawService;
import com.microbank.account.application.WithdrawalRequest;
import com.microbank.account.domain.Transaction;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Validated
@Tag(name = "Deposit/Withdraw Management", description = "API para depósitos y retiros bancarios")
public class DepositWithdrawController {

    private static final Logger log = LoggerFactory.getLogger(DepositWithdrawController.class);

    @Autowired
    private DepositWithdrawService depositWithdrawService;

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Realizar un depósito", description = "Deposita fondos en una cuenta bancaria con validaciones de límite de operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Depósito realizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (monto inválido, límite de operación excedido, cuenta inactiva)"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody DepositRequest request) {
        log.info("Solicitud de depósito. Account ID: {}, Amount: {}", accountId, request.getAmount());

        request.setAccountId(accountId);
        Transaction transaction = depositWithdrawService.deposit(request);
        TransactionResponse response = TransactionResponse.from(transaction);

        log.info("Depósito ejecutado exitosamente. TransactionID: {}", transaction.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Realizar un retiro", description = "Retira fondos de una cuenta bancaria con validaciones de balance y límite de operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro realizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (monto inválido, balance insuficiente, límite de operación excedido, cuenta inactiva)"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody WithdrawalRequest request) {
        log.info("Solicitud de retiro. Account ID: {}, Amount: {}", accountId, request.getAmount());

        request.setAccountId(accountId);
        Transaction transaction = depositWithdrawService.withdrawal(request);
        TransactionResponse response = TransactionResponse.from(transaction);

        log.info("Retiro ejecutado exitosamente. TransactionID: {}", transaction.getId());
        return ResponseEntity.ok(response);
    }
}
