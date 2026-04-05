# MicroBank API REST - Documentación de Endpoints

## Información General
- **Base URL:** `http://localhost:8080/api/v1`
- **Versión:** v1.0.0
- **Documentación interactiva:** Swagger UI en `/swagger-ui/index.html`
- **Formato de respuestas:** JSON
- **Autenticación:** No implementada (fase actual de desarrollo)

---

## 📋 Tabla de Contenidos
1. [Health Check](#health-check)
2. [Account Management](#account-management-req-010)
3. [Transfers](#transfers-req-011)
4. [Deposits & Withdrawals](#deposits--withdrawals-req-012)
5. [Audit (Admin)](#audit-admin-req-013)

---

## Health Check

### GET /health
Verifica el estado de la aplicación.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/health` |
| **Status** | 200 |
| **Respuesta** | Plain text: `MicroBank is running!` |

---

## Account Management (REQ-010)

### 1. POST /accounts
Crea una nueva cuenta bancaria.

| Propiedad | Valor |
|---|---|
| **Método** | POST |
| **Path** | `/api/v1/accounts` |
| **Status** | 201 Created |
| **Request Body** | `CreateAccountRequest` |
| **Response** | `AccountResponse` |

**Request:**
```json
{
  "accountType": "AHORRO",
  "initialBalance": 1000.00
}
```

**Response (201):**
```json
{
  "id": "uuid",
  "accountNumber": "ACC...",
  "accountType": "AHORRO",
  "balance": 1000.00,
  "status": "ACTIVO",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:00:00"
}
```

---

### 2. GET /accounts
Lista todas las cuentas.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/accounts` |
| **Status** | 200 OK |
| **Response** | `Array<AccountResponse>` |

---

### 3. GET /accounts/{id}
Obtiene una cuenta por ID.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/accounts/{id}` |
| **Parámetros** | `id` (UUID, path) |
| **Status** | 200 OK / 404 Not Found |
| **Response** | `AccountResponse` |

---

### 4. GET /accounts/number/{accountNumber}
Obtiene una cuenta por número.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/accounts/number/{accountNumber}` |
| **Parámetros** | `accountNumber` (string, path) |
| **Status** | 200 OK / 404 Not Found |
| **Response** | `AccountResponse` |

---

### 5. PUT /accounts/{id}/status
Cambia el estado de una cuenta.

| Propiedad | Valor |
|---|---|
| **Método** | PUT |
| **Path** | `/api/v1/accounts/{id}/status` |
| **Parámetros** | `id` (UUID, path) |
| **Status** | 200 OK / 400 Bad Request / 404 Not Found |
| **Request Body** | `UpdateAccountStatusRequest` |
| **Response** | `AccountResponse` |

**Request:**
```json
{
  "newStatus": "INACTIVO"
}
```

**Estados válidos:** `ACTIVO`, `INACTIVO`, `BLOQUEADO`, `SUSPENDIDO`

---

## Transfers (REQ-011)

### 1. POST /transfers
Realiza una transferencia entre cuentas.

| Propiedad | Valor |
|---|---|
| **Método** | POST |
| **Path** | `/api/v1/transfers` |
| **Status** | 201 Created / 400 Bad Request / 404 Not Found / 409 Conflict |
| **Request Body** | `TransferRequest` |
| **Response** | `TransactionResponse` |

**Request:**
```json
{
  "sourceAccountId": "uuid-source",
  "targetAccountId": "uuid-target",
  "amount": 500.00
}
```

**Response (201):**
```json
{
  "id": "transaction-uuid",
  "sourceAccountId": "uuid-source",
  "targetAccountId": "uuid-target",
  "amount": 500.00,
  "type": "TRANSFERENCIA",
  "status": "COMPLETADA",
  "description": "Transferencia completada",
  "createdAt": "2026-04-05T11:00:00"
}
```

**Errores comunes:**
- `400` — Monto inválido, fondos insuficientes, misma cuenta origen/destino
- `404` — Una o ambas cuentas no encontradas
- `409` — Conflicto de concurrencia (cuenta bloqueada)

---

### 2. GET /transfers/{id}
Obtiene los detalles de una transferencia.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/transfers/{id}` |
| **Parámetros** | `id` (UUID, path) |
| **Status** | 200 OK / 404 Not Found |
| **Response** | `TransactionResponse` |

---

### 3. GET /transfers/account/{accountId}
Lista todas las transferencias de una cuenta.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/transfers/account/{accountId}` |
| **Parámetros** | `accountId` (UUID, path) |
| **Status** | 200 OK |
| **Response** | `Array<TransactionResponse>` |

---

## Deposits & Withdrawals (REQ-012)

### 1. POST /accounts/{id}/deposit
Realiza un depósito a una cuenta.

| Propiedad | Valor |
|---|---|
| **Método** | POST |
| **Path** | `/api/v1/accounts/{id}/deposit` |
| **Parámetros** | `id` (UUID, path) |
| **Status** | 201 Created / 400 Bad Request / 404 Not Found |
| **Request Body** | `DepositRequest` |
| **Response** | `TransactionResponse` |

**Request:**
```json
{
  "accountId": "uuid",
  "amount": 500.00
}
```

**Límites por tipo de cuenta:**
- AHORRO: $2,000.00
- CORRIENTE: $5,000.00
- CRÉDITO: $10,000.00

**Validaciones:**
- Monto > 0
- Cuenta debe estar ACTIVA
- No debe exceder límite de depósito

**Errores:**
- `400` — Monto inválido, cuenta inactiva, límite excedido
- `404` — Cuenta no encontrada

---

### 2. POST /accounts/{id}/withdraw
Realiza un retiro de una cuenta.

| Propiedad | Valor |
|---|---|
| **Método** | POST |
| **Path** | `/api/v1/accounts/{id}/withdraw` |
| **Parámetros** | `id` (UUID, path) |
| **Status** | 200 OK / 400 Bad Request / 404 Not Found |
| **Request Body** | `WithdrawalRequest` |
| **Response** | `TransactionResponse` |

**Request:**
```json
{
  "accountId": "uuid",
  "amount": 200.00
}
```

**Límites por tipo de cuenta:**
- AHORRO: $2,000.00
- CORRIENTE: $5,000.00
- CRÉDITO: $10,000.00

**Validaciones:**
- Monto > 0
- Cuenta debe estar ACTIVA
- Balance suficiente
- No debe exceder límite de retiro

**Errores:**
- `400` — Monto inválido, balance insuficiente, cuenta inactiva, límite excedido
- `404` — Cuenta no encontrada

---

## Audit (Admin) (REQ-013)

### 1. GET /admin/audit/account/{accountId}
Lista los logs de auditoría de una cuenta.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/admin/audit/account/{accountId}` |
| **Parámetros** | `accountId` (UUID, path) |
| **Status** | 200 OK |
| **Response** | `Array<AuditLogResponse>` |

**Response:**
```json
[
  {
    "id": "audit-uuid",
    "accountId": "account-uuid",
    "action": "TRANSFERENCIA",
    "details": "Transferencia de 500 desde ACC... hacia ACC...",
    "userId": null,
    "timestamp": "2026-04-05T11:00:00"
  }
]
```

**Acciones auditadas:**
- `CREAR_CUENTA` — Creación de cuenta
- `TRANSFERENCIA` — Transferencia entre cuentas
- `DEPOSITO` — Depósito
- `RETIRO` — Retiro

---

### 2. GET /admin/audit
Lista todos los logs de auditoría con paginación.

| Propiedad | Valor |
|---|---|
| **Método** | GET |
| **Path** | `/api/v1/admin/audit` |
| **Parámetros Query** | `page` (int, default=0), `size` (int, default=20), `sort` (default=timestamp,desc) |
| **Status** | 200 OK |
| **Response** | `Page<AuditLogResponse>` |

**Request:**
```
GET /api/v1/admin/audit?page=0&size=20&sort=timestamp,desc
```

**Response:**
```json
{
  "content": [
    {
      "id": "audit-uuid",
      "accountId": "account-uuid",
      "action": "TRANSFERENCIA",
      "details": "...",
      "userId": null,
      "timestamp": "2026-04-05T11:00:00"
    }
  ],
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 3,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 3,
  "first": true,
  "empty": false
}
```

---

## Códigos de Error Estándar

| Status | Error | Descripción |
|---|---|---|
| 200 | OK | Solicitud exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Solicitud inválida (validación, monto inválido, etc.) |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto de concurrencia (cuenta bloqueada) |
| 500 | Internal Server Error | Error interno del servidor |

**Formato de error:**
```json
{
  "timestamp": "2026-04-05T11:00:00",
  "status": 400,
  "error": "INVALID_ACCOUNT",
  "message": "El balance inicial debe ser mayor a cero",
  "path": "/api/v1/accounts"
}
```

---

## DTOs Principales

### AccountResponse
```json
{
  "id": "uuid",
  "accountNumber": "ACC...",
  "accountType": "AHORRO",
  "balance": 1000.00,
  "status": "ACTIVO",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:00:00"
}
```

### TransactionResponse
```json
{
  "id": "uuid",
  "sourceAccountId": "uuid",
  "targetAccountId": "uuid",
  "amount": 500.00,
  "type": "TRANSFERENCIA",
  "status": "COMPLETADA",
  "description": "Transferencia completada",
  "createdAt": "2026-04-05T11:00:00"
}
```

### AuditLogResponse
```json
{
  "id": "uuid",
  "accountId": "uuid",
  "action": "TRANSFERENCIA",
  "details": "Transferencia de 500 desde...",
  "userId": null,
  "timestamp": "2026-04-05T11:00:00"
}
```

---

## Resumen de Endpoints

| Endpoint | Método | Status | Descripción |
|---|---|---|---|
| `/health` | GET | 200 | Health check |
| `/accounts` | POST | 201 | Crear cuenta |
| `/accounts` | GET | 200 | Listar cuentas |
| `/accounts/{id}` | GET | 200/404 | Obtener cuenta por ID |
| `/accounts/number/{number}` | GET | 200/404 | Obtener cuenta por número |
| `/accounts/{id}/status` | PUT | 200/400/404 | Cambiar estado |
| `/transfers` | POST | 201/400/404/409 | Realizar transferencia |
| `/transfers/{id}` | GET | 200/404 | Obtener transferencia |
| `/transfers/account/{id}` | GET | 200 | Transferencias de cuenta |
| `/accounts/{id}/deposit` | POST | 201/400/404 | Realizar depósito |
| `/accounts/{id}/withdraw` | POST | 200/400/404 | Realizar retiro |
| `/admin/audit/account/{id}` | GET | 200 | Logs de cuenta (Admin) |
| `/admin/audit` | GET | 200 | Todos los logs (Admin) |

**Total: 13 endpoints**

---

*Última actualización: 5 de abril, 2026*
*Documentación generada para MicroBank Core Banking Simulator v0.2.0*
