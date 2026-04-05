package com.microbank.account.presentation;

import com.microbank.account.application.AccountService;
import com.microbank.account.application.CreateAccountRequest;
import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountStatus;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta", description = "Crea una nueva cuenta bancaria con el tipo y balance inicial especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (balance debe ser mayor a cero, tipo de cuenta requerido)")
    })
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Solicitud para crear nueva cuenta. Tipo: {}, Balance: {}", request.getAccountType(), request.getInitialBalance());

        Account account = accountService.createAccount(request);
        AccountResponse response = AccountResponse.from(account);

        log.info("Cuenta creada exitosamente. ID: {}", account.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene un listado de todas las cuentas del sistema")
    @ApiResponse(responseCode = "200", description = "Listado de cuentas obtenido exitosamente")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        log.debug("Solicitando listado de todas las cuentas");

        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponse> responses = accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        log.info("Se retornaron {} cuentas", responses.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", description = "Obtiene los detalles de una cuenta específica por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable("id") UUID id) {
        log.debug("Solicitando cuenta con ID: {}", id);

        Account account = accountService.getAccountById(id);
        AccountResponse response = AccountResponse.from(account);

        log.info("Cuenta obtenida. ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Obtener cuenta por número", description = "Obtiene los detalles de una cuenta específica por su número de cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable("accountNumber") String accountNumber) {
        log.debug("Solicitando cuenta con número: {}", accountNumber);

        Account account = accountService.getAccountByNumber(accountNumber);
        AccountResponse response = AccountResponse.from(account);

        log.info("Cuenta obtenida. Número: {}", accountNumber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de cuenta", description = "Actualiza el estado de una cuenta (ej: ACTIVO a INACTIVO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o estado no permitido"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        log.info("Solicitud para actualizar estado de cuenta. ID: {}, Nuevo estado: {}", id, request.getNewStatus());

        Account account = accountService.updateAccountStatus(id, request.getNewStatus());
        AccountResponse response = AccountResponse.from(account);

        log.info("Estado de cuenta actualizado. ID: {}, Nuevo estado: {}", id, request.getNewStatus());
        return ResponseEntity.ok(response);
    }
}
