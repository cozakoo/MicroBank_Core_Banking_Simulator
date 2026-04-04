# ADR-003: Estrategia de Manejo de Errores

**Estado:** Aceptado
**Fecha:** Abril 2026
**Autor:** Martín Arcos Vargas

## Contexto

MicroBank requiere un manejo robusto de errores que:
1. Sea consistente en toda la aplicación
2. Diferencie entre errores de dominio (negocio) vs técnicos (infraestructura)
3. Devuelva respuestas HTTP significativas
4. Registre en auditoría los fallos de transacciones

## Decisión

Se implementará una estrategia de **tres niveles de excepciones** con manejo centralizado via `GlobalExceptionHandler`:

### 1. Excepciones de Dominio
Errores de lógica de negocio. Esperados y manejables.

```java
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID accountId, BigDecimal balance, BigDecimal requested) {
        super(String.format("Cuenta %s: balance insuficiente. Disponible: %s, Solicitado: %s",
            accountId, balance, requested));
    }
}

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super("Cuenta no encontrada: " + accountId);
    }
}
```

### 2. Excepciones Técnicas
Errores de infraestructura (BD, red, etc.). **No** lanzar excepciones técnicas genéricas.

```java
public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 3. GlobalExceptionHandler
Controller advice que mapea excepciones a respuestas HTTP.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(400)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(404)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        // Log para debugging interno
        logger.error("Error no controlado", ex);

        ErrorResponse error = ErrorResponse.builder()
            .status(500)
            .message("Error interno del servidor")
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(500).body(error);
    }
}
```

### 4. Mapeo de Excepciones a HTTP

| Excepción de Dominio | HTTP Status | Significado |
|---|---|---|
| `InsufficientFundsException` | 400 Bad Request | El cliente envió datos que violan reglas de negocio |
| `AccountNotFoundException` | 404 Not Found | El recurso no existe |
| `InvalidTransactionException` | 400 Bad Request | Parámetros inválidos |
| **Error técnico no controlado** | 500 Internal Server Error | Falla del servidor |

## Estructura de Respuesta de Error

```json
{
  "status": 400,
  "message": "Cuenta a4f8e7c9-1234-5678-90ab-cdef12345678: balance insuficiente. Disponible: 100.00, Solicitado: 150.00",
  "timestamp": "2026-04-04T15:30:45.123456",
  "code": "INSUFFICIENT_FUNDS"
}
```

## Auditoría de Errores

- **Excepciones de dominio:** Se registran como transacciones fallidas en `audit_logs` con status `FAILED`
- **Excepciones técnicas:** Se logean con stack trace completo para debugging

```java
@Transactional
public Transfer executeTransfer(UUID fromId, UUID toId, BigDecimal amount) {
    try {
        // ... lógica de transferencia ...
    } catch (InsufficientFundsException e) {
        Transfer failedTransfer = new Transfer(...);
        failedTransfer.setStatus(TransactionStatus.FAILED);
        failedTransfer.setDescription(e.getMessage());
        transferRepository.save(failedTransfer);
        auditService.logFailure(failedTransfer, e);
        throw e;
    }
}
```

## Consecuencias

### Positivas
- **Consistencia:** Todos los errores se manejan de la misma manera
- **Claridad:** Excepciones de dominio comunican reglas de negocio
- **Debugging fácil:** Errores técnicos se logean con contexto completo

### Negativas
- Boilerplate inicial (excepciones personalizadas por cada escenario)
- Requiere disciplina de los desarrolladores (no lanzar `RuntimeException` genérica)

## Referencias

- Spring Framework: [Exception Handling in Spring Boot REST API](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- Domain-Driven Design: Exception Handling in Aggregates
