# MicroBank API - Documentación Swagger/OpenAPI (REQ-017)

## 🚀 Acceso a Swagger UI

### URLs disponibles

| Recurso | URL |
|---------|-----|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` |

### 📋 Requisitos

1. Aplicación levantada localmente:
   ```bash
   mvn spring-boot:run
   ```

2. Base de datos PostgreSQL corriendo (o usar Docker):
   ```bash
   docker-compose up postgres -d
   ```

---

## 📖 Documentación de la API

### Acceso a Swagger UI

1. **Levanta la aplicación:**
   ```bash
   # Terminal 1: Levanta la BD
   docker-compose up postgres pgadmin -d

   # Terminal 2: Levanta la app
   mvn spring-boot:run
   ```

2. **Abre el navegador:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Verás:**
   - ✅ Todos los endpoints documentados (13 total)
   - ✅ Modelos/DTOs con ejemplos JSON
   - ✅ Validaciones y restricciones
   - ✅ Códigos de error (200, 201, 400, 404, 409, 500)
   - ✅ Prueba interactiva de cada endpoint

---

## 🧪 Testing de Endpoints desde Swagger UI

### Flujo de prueba recomendado

**1. Account Management (REQ-010)**

```
POST /api/v1/accounts (crear cuenta)
├─ GET /api/v1/accounts (listar todas)
├─ GET /api/v1/accounts/{id} (obtener por ID)
├─ GET /api/v1/accounts/number/{accountNumber} (obtener por número)
└─ PUT /api/v1/accounts/{id}/status (cambiar estado)
```

**Ejemplo práctico:**
- Crea 2 cuentas con `POST /accounts`
- Lista todas con `GET /accounts`
- Obtén una por ID con `GET /accounts/{id}`
- Cambia estado a INACTIVO con `PUT /accounts/{id}/status`

---

**2. Transfers (REQ-011)**

```
POST /api/v1/transfers (transferencia)
├─ GET /api/v1/transfers/{id} (obtener transferencia)
└─ GET /api/v1/transfers/account/{accountId} (listar de cuenta)
```

**Ejemplo práctico:**
- Transfiere 500 de cuenta1 a cuenta2
- Obtén el ID de la transacción
- Consulta los detalles con `GET /transfers/{id}`
- Lista todas las transferencias de cuenta1

---

**3. Deposits & Withdrawals (REQ-012)**

```
POST /api/v1/accounts/{id}/deposit (depósito)
└─ POST /api/v1/accounts/{id}/withdraw (retiro)
```

**Ejemplo práctico:**
- Deposita 1000 a cuenta1
- Retira 500 de cuenta1
- Verifica el balance final con `GET /accounts/{id}`

---

**4. Audit (REQ-013)**

```
GET /api/v1/admin/audit/account/{accountId} (logs de cuenta)
└─ GET /api/v1/admin/audit (todos los logs, paginado)
```

**Ejemplo práctico:**
- Realiza varias operaciones (transferencias, depósitos, etc.)
- Consulta `GET /admin/audit/account/{id}` para ver el historial de una cuenta
- Usa `GET /admin/audit?page=0&size=20` para todos los logs

---

## 📊 Información Meta de la API

### Contacto
```
Nombre: MicroBank Development Team
Email: martinarcosvargas2@gmail.com
GitHub: https://github.com/cozakoo/MicroBank_Core_Banking_Simulator
```

### Servidores
```
Desarrollo: http://localhost:8080
Producción: https://api.microbank.dev
```

### Versión
```
API v1.0.0
Implementación: v0.2.0
```

---

## 🔧 Configuración de Swagger en application.yml

La configuración actual en `src/main/resources/application.yml`:

```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    tagsSorter: alpha              # Ordena tags alfabéticamente
    operationsSorter: alpha         # Ordena operaciones alfabéticamente
    displayOperationId: true        # Muestra IDs de operaciones
    defaultModelsExpandDepth: 1     # Profundidad de expansión de modelos
    defaultModelExpandDepth: 1      # Profundidad de expansion de atributos
  api-docs:
    path: /v3/api-docs
  show-actuator: false             # Oculta endpoints de actuator
```

---

## 📥 Exportar Documentación

### Exportar OpenAPI JSON

```bash
# Linux/Mac
curl http://localhost:8080/v3/api-docs > microbank-api.json

# Windows PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/v3/api-docs" -OutFile "microbank-api.json"

# O desde Swagger UI: botón "Download" en la esquina superior derecha
```

### Usar JSON en herramientas externas

```bash
# Importar en Postman
1. Abre Postman
2. File → Import → Select Files
3. Selecciona microbank-api.json
4. Se crearán automáticamente todas las requests

# Importar en Insomnia
1. Abre Insomnia
2. Import → From URL
3. Pega: http://localhost:8080/v3/api-docs
4. Se sincroniza automáticamente con los cambios
```

---

## 🎯 Esquemas de Respuesta Documentados

### AccountResponse
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "accountNumber": "ACC1111111111111",
  "accountType": "CORRIENTE",
  "balance": 5000.00,
  "status": "ACTIVO",
  "createdAt": "2026-04-05T12:30:00",
  "updatedAt": "2026-04-05T12:30:00"
}
```

### TransactionResponse
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440001",
  "targetAccountId": "550e8400-e29b-41d4-a716-446655440002",
  "amount": 500.00,
  "type": "TRANSFERENCIA",
  "status": "COMPLETADA",
  "description": "Transferencia exitosa",
  "createdAt": "2026-04-05T12:30:00"
}
```

### AuditLogResponse
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "accountId": "550e8400-e29b-41d4-a716-446655440001",
  "action": "TRANSFERENCIA",
  "details": "Transferencia de 500.00 desde ACC1111111111111 hacia ACC2222222222222",
  "userId": null,
  "timestamp": "2026-04-05T12:30:00"
}
```

---

## ✅ Condiciones de Aceptación Verificadas

| Condición | Estado | Cómo verificar |
|-----------|--------|---|
| ✓ Swagger UI accesible | ✅ | Abre `http://localhost:8080/swagger-ui.html` |
| ✓ OpenAPI JSON disponible | ✅ | Accede a `http://localhost:8080/v3/api-docs` |
| ✓ Todos los endpoints documentados | ✅ | Verifica 13 endpoints en Swagger UI |
| ✓ Parámetros con ejemplos | ✅ | Cada request DTO tiene `@Schema(example = "...")` |
| ✓ Validaciones documentadas | ✅ | Anotaciones de validación visibles en UI |
| ✓ Respuestas con ejemplos JSON | ✅ | Cada response DTO tiene ejemplos |
| ✓ Códigos de error documentados | ✅ | Usa `@ApiResponse` con status codes |

---

## 🚀 Próximos pasos

1. **Levanta la API:** `mvn spring-boot:run`
2. **Abre Swagger:** `http://localhost:8080/swagger-ui.html`
3. **Prueba un endpoint:** Haz clic en cualquier operación, presiona "Try it out", completa los campos
4. **Exporta documentación:** Descarga el JSON y comparte con el equipo

---

*Documentación generada para REQ-017: Documentación API - Swagger/OpenAPI*
*MicroBank Core Banking Simulator v0.2.0*
*5 de abril, 2026*
