package com.microbank.account.presentation;

import com.microbank.account.application.AccountService;
import com.microbank.account.application.CreateAccountRequest;
import com.microbank.account.domain.Account;
import com.microbank.auth.domain.UserRepository;
import com.microbank.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
@Validated
@Tag(name = "Account Management", description = "API para gestión de cuentas bancarias")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta (asignada al usuario autenticado)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cuenta creada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow().getId();

        Account account = accountService.createAccount(request, ownerId);
        AccountResponse response = AccountResponse.from(account);

        log.info("Cuenta creada: {} para usuario: {}", account.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Listar mis cuentas (del usuario autenticado)")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<AccountResponse> responses = accountService.getAccountsByOwner(userDetails.getUsername())
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable("id") UUID id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(AccountResponse.from(account)));
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Obtener cuenta por número")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @PathVariable("accountNumber") String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(AccountResponse.from(account)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de cuenta")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAccountStatusRequest request) {

        Account account = accountService.updateAccountStatus(id, request.getNewStatus());
        return ResponseEntity.ok(ApiResponse.success(AccountResponse.from(account)));
    }

    @PutMapping("/{id}/alias")
    @Operation(summary = "Asignar o cambiar el alias de una cuenta")
    public ResponseEntity<ApiResponse<AccountResponse>> setAlias(
            @PathVariable("id") UUID id,
            @Valid @RequestBody SetAliasRequest request) {

        Account account = accountService.setAlias(id, request.getAlias());
        return ResponseEntity.ok(ApiResponse.success(AccountResponse.from(account)));
    }

    @GetMapping("/search/{identifier}")
    @Operation(summary = "Buscar cuenta por número o alias")
    public ResponseEntity<ApiResponse<AccountResponse>> getByNumberOrAlias(
            @PathVariable("identifier") String identifier) {
        Account account = accountService.getAccountByNumberOrAlias(identifier);
        return ResponseEntity.ok(ApiResponse.success(AccountResponse.from(account)));
    }
}
