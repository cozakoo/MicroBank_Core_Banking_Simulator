package com.microbank.shared.api;

import com.microbank.shared.response.ApiResponse;
import com.microbank.shared.exceptions.AccountLockedException;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InactiveAccountException;
import com.microbank.shared.exceptions.InsufficientFundsException;
import com.microbank.shared.exceptions.InvalidAccountException;
import com.microbank.shared.exceptions.OperationLimitExceededException;
import com.microbank.shared.exceptions.InvalidAmountException;
import com.microbank.shared.exceptions.InvalidEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * MANEJADOR CENTRAL DE EXCEPCIONES (Martín)
 *
 * ESTRATEGIA DE ERRORES:
 * - @RestControllerAdvice: TODOS los Controllers pasan por aquí
 * - NO dejamos que excepciones sin manejo lleguen al cliente (500 genérico)
 * - Mapeo de HTTP Status codes:
 *   - 400 Bad Request: Validación fallida, request inválido, fondos insuficientes
 *   - 404 Not Found: Cuenta/Transferencia no existe
 *   - 409 Conflict: Cuenta bloqueada (contención de locks, timeout)
 *   - 500 Internal Server Error: Errores no controlados (bugs)
 * - Auditoría: CADA error se loguea (warn para esperados, error para inesperados)
 * - Frontend: Siempre recibe {success: false, error: "descripción amigable"}
 *
 * IMPORTANTE: No exponemos detalles internos de BD, stack traces, etc.
 * (El cliente NO necesita saber si es un timeout de BD o un deadlock)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Error 404 - Cuenta no encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidAccountException.class,
            InsufficientFundsException.class,
            InactiveAccountException.class,
            InvalidAmountException.class,
            InvalidEmailException.class
    })
    public ResponseEntity<ApiResponse<?>> handleBadRequestExceptions(RuntimeException ex) {
        log.warn("Error 400 - Solicitud inválida: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(OperationLimitExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleOperationLimitExceeded(OperationLimitExceededException ex) {
        log.warn("Error 400 - Límite excedido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountLocked(AccountLockedException ex) {
        log.warn("Error 409 - Conflicto/Cuenta bloqueada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Error 400 - Validación fallida: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Errores de validación: " + message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Error 500 - Error interno no manejado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Ocurrió un error inesperado en el simulador."));
    }
}