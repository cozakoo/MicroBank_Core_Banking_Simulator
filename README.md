# MicroBank Core Banking Simulator

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%2B%20Mockito-25A162?style=flat-square)](https://junit.org/junit5/)
[![Coverage](https://img.shields.io/badge/Coverage->80%25-4CAF50?style=flat-square)](https://www.jacoco.org/)
[![Status](https://img.shields.io/badge/Status-✅%20Completado-green?style=flat-square)](https://github.com/cozakoo/MicroBank_Core_Banking_Simulator)
[![Version](https://img.shields.io/badge/Version-v0.4.0-blue?style=flat-square)](https://github.com/cozakoo/MicroBank_Core_Banking_Simulator/releases)

## 👥 Colaboradores

<a href="https://github.com/cozakoo/MicroBank_Core_Banking_Simulator/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=cozakoo/MicroBank_Core_Banking_Simulator" />
</a>


---

## 🏦 Descripción del Proyecto

MicroBank es un simulador de banca central de alto rendimiento diseñado para manejar operaciones financieras críticas con precisión absoluta. Este proyecto demuestra principios avanzados de ingeniería de software incluyendo **Domain-Driven Design (DDD)**, **integridad ACID** y **gestión robusta de concurrencia** mediante Pessimistic Locking a nivel de base de datos.

El simulador permite gestionar cuentas bancarias, ejecutar transferencias seguras entre cuentas, realizar depósitos y retiros, y mantener un registro completo de auditoría de cada transacción, asegurando cumplimiento e integridad de datos en todo momento. Incluye un **dashboard administrativo** completo para gestionar todas las operaciones de forma intuitiva.

## 🎯 Objetivos Clave

- **Integridad ACID**: Garantizar que todas las operaciones financieras sean atómicas, consistentes, aisladas y duraderas.
- **Arquitectura DDD**: Implementación de separación clara de responsabilidades usando patrones de Domain-Driven Design.
- **Procesamiento Concurrente**: Manejo de race conditions en transferencias usando pessimistic locking a nivel de BD.
- **Auditoría Completa**: Cada operación genera un registro inmutable de auditoría para trazabilidad y cumplimiento normativo.
- **Experiencia del Desarrollador**: Entorno dockerizado con setup de un comando.
- **Excelencia en Testing**: Cobertura alta con tests unitarios, de integración (TestContainers) y estrés.
- **API RESTful**: API limpia y documentada con OpenAPI/Swagger.
- **Dashboard Intuitivo**: Interfaz administrativa moderna para todas las operaciones financieras.

## 🏗 Arquitectura

El proyecto sigue un enfoque de **Domain-Driven Design (DDD)** para manejar la complejidad:

```
┌─────────────────────────────────────────────────────────┐
│         Admin Dashboard (HTML5 + Bootstrap 5)           │
│      (AccountController, DepositWithdraw, Transfers)    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│             REST API Controllers                        │
│  (Account, Transfer, Deposit/Withdraw, Audit)          │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│            Domain Services                             │
│  (AccountService, TransferService, AuditService)       │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────────┐    ┌──────▼────────────┐
│  Domain Entities   │    │ Data Access (JPA) │
│  & Business Logic  │    │ & Repositories    │
└────────────────────┘    └──────┬────────────┘
                                 │
                        ┌────────▼────────┐
                        │  PostgreSQL 15  │
                        │  (Persistence)  │
                        └─────────────────┘
```

## 🚀 Inicio Rápido

### Requisitos Previos

- **Java 17+** — [Descargar Eclipse Temurin](https://adoptium.net/)
- **Maven 3.8+** — `mvn -version` para verificar
- **PostgreSQL 15** — Instalado localmente (para ejecución local)
- **Docker & Docker Compose** — Para ejecución en contenedores

### Opción 1: Ejecución Local

Ideal para desarrollo y debugging.

```bash
# 1. Navega al directorio del proyecto
cd MicroBank_Core_Banking_Simulator

# 2. Verifica que PostgreSQL está corriendo
psql -U postgres -d postgres -c "SELECT 1"

# 3. Descarga dependencias e instala
mvn clean install

# 4. Ejecuta la aplicación
mvn spring-boot:run

# 5. Verifica que está corriendo
curl http://localhost:8080/api/v1/health
# Respuesta esperada: "MicroBank is running!"
```

**Detener**: `Ctrl+C` en la terminal

---

### Opción 2: Ejecución en Docker (Recomendada)

Levantar toda la infraestructura con un comando.

```bash
# 1. Navega al directorio del proyecto
cd MicroBank_Core_Banking_Simulator

# 2. Construye e inicia todos los servicios
docker-compose up --build

# 3. Verifica que está corriendo
curl http://localhost:8080/api/v1/health
# Respuesta esperada: "MicroBank is running!"

# 4. Detener
Ctrl+C
docker-compose down
```

**Servicios disponibles:**
- 🌐 **MicroBank API**: http://localhost:8080
- 📊 **Dashboard Admin**: http://localhost:8080/ (con interfaz gráfica)
- 📄 **Swagger UI**: http://localhost:8080/swagger-ui.html
- 🗄 **pgAdmin** (Gestión de BD): http://localhost:5050
  - Email: `admin@microbank.com`
  - Contraseña: `admin`
- 🗄 **PostgreSQL**: `localhost:5432`
  - Usuario: `postgres`
  - Contraseña: `postgres`

---

### Opción 3: Híbrido (App Local + BD en Docker)

Si necesitas la BD en Docker pero quieres ejecutar la app localmente.

```bash
# 1. Inicia solo PostgreSQL y pgAdmin
docker-compose up postgres pgadmin -d

# 2. Ejecuta la app localmente
mvn spring-boot:run

# 3. Detener
docker-compose down
```

---

## 📊 Dashboard Administrativo

Una vez que la aplicación esté corriendo, accede al **Dashboard Admin** completo:

```
http://localhost:8080/
```

### ✨ Funcionalidades

- **Crear Cuentas** — Formulario modal para nuevas cuentas (CORRIENTE, AHORRO, CRÉDITO)
- **💰 Depositar** — Ingresar dinero a cualquier cuenta
- **💸 Retirar** — Extraer fondos (con validación de saldo)
- **🔄 Transferir** — Transferencias entre cuentas con locking ACID
- **📊 Ver Transacciones** — Historial completo de cada cuenta
- **⚙️ Cambiar Estado** — Activar/Suspender/Cerrar cuentas
- **🔍 Buscar** — Búsqueda en tiempo real por número de cuenta
- **📈 Estadísticas** — Contadores de cuentas totales, activas y suspendidas

**Más detalles:** Lee [DASHBOARD.md](DASHBOARD.md)

---

## 📚 Documentación Interactiva de API (Swagger/OpenAPI)

Una vez que la aplicación esté corriendo, puedes explorar y probar todos los endpoints de forma interactiva:

### 🌐 Acceso a Swagger UI

```
http://localhost:8080/swagger-ui.html
```

**Características:**
- ✅ 13 endpoints completamente documentados (Accounts, Transfers, Deposits, Audit)
- ✅ Ejemplos de solicitud/respuesta en tiempo real
- ✅ Validaciones y restricciones de parámetros
- ✅ Prueba interactiva: "Try it out" para ejecutar directamente desde el navegador
- ✅ Códigos de respuesta HTTP documentados (200, 201, 400, 404, 409, 500)

### 📋 Endpoints Disponibles

#### 1️⃣ **Account Management** (5 endpoints)
```
GET    /api/v1/accounts                    — Listar todas las cuentas
GET    /api/v1/accounts/{id}               — Obtener cuenta por ID
GET    /api/v1/accounts/number/{number}    — Obtener cuenta por número
POST   /api/v1/accounts                    — Crear nueva cuenta
PUT    /api/v1/accounts/{id}/status        — Cambiar estado de cuenta
```

#### 2️⃣ **Transfers** (3 endpoints)
```
POST   /api/v1/transfers                   — Realizar transferencia
GET    /api/v1/transfers/{id}              — Obtener detalles de transferencia
GET    /api/v1/transfers/account/{id}      — Listar transferencias de cuenta
```

#### 3️⃣ **Deposits & Withdrawals** (2 endpoints)
```
POST   /api/v1/accounts/{id}/deposit       — Realizar depósito
POST   /api/v1/accounts/{id}/withdraw      — Realizar retiro
```

#### 4️⃣ **Audit (Admin)** (2 endpoints)
```
GET    /api/v1/admin/audit                 — Listar todos los registros de auditoría
GET    /api/v1/admin/audit/account/{id}    — Listar auditoría de cuenta específica
```

### 🚀 Ejemplo Práctico en Swagger

1. **Abre** `http://localhost:8080/swagger-ui.html` en tu navegador
2. **Expande** una sección de endpoints (ej: `Account Management`)
3. **Haz clic** en un endpoint (ej: `POST /api/v1/accounts`)
4. **Presiona** botón "Try it out"
5. **Completa** los parámetros con valores de ejemplo
6. **Presiona** "Execute"
7. **Verás** la respuesta en tiempo real

### 📊 Exportar Documentación OpenAPI

Para usar en Postman, Insomnia, u otras herramientas:

```bash
# Descargar JSON
curl http://localhost:8080/v3/api-docs > microbank-api.json

# O acceder a YAML
http://localhost:8080/v3/api-docs.yaml
```

Luego importa en tu herramienta favorita y prueba los endpoints localmente.

**Más detalles:** Lee [docs/SWAGGER_SETUP.md](docs/SWAGGER_SETUP.md)

---

## 🛠 Stack Tecnológico

| Categoría | Tecnología |
|:---|:---|
| **Lenguaje** | Java 17 |
| **Framework** | Spring Boot 3.2.x |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Validación** | Jakarta Validation (spring-boot-starter-validation) |
| **Base de Datos** | PostgreSQL 15 |
| **Containerización** | Docker / Docker Compose |
| **Documentación API** | Springdoc-OpenAPI (Swagger) |
| **Frontend** | HTML5 + Bootstrap 5 + JavaScript Vanilla |
| **Testing** | JUnit 5, Mockito, H2 (tests), TestContainers |
| **CI/CD** | GitHub Actions |
| **Build** | Maven 3.8+ |

## 🧪 Testing

Priorizamos la calidad del código mediante una estrategia de testing integral:

- **Tests Unitarios**: Pruebas de la lógica de dominio aislada.
- **Tests de Integración**: Validación end-to-end usando TestContainers para PostgreSQL.
- **Tests de Rendimiento**: Simulación de transferencias concurrentes para verificar mecanismos de locking.

### Ejecutar Tests Localmente

```bash
# Todos los tests
mvn test

# Con reporte de cobertura
mvn test jacoco:report
# Reporte en: target/site/jacoco/index.html

# Solo tests de dominio
mvn test -Dtest=**/*Test.java

# Solo tests de integración
mvn test -Dtest=**/*IntegrationTest.java
```

---

## 📚 Estructura del Proyecto

```
microbank/
├── src/main/java/com/microbank/
│   ├── MicrobankApplication.java          # Entry point
│   ├── api/
│   │   ├── controller/                    # Controllers REST
│   │   ├── dto/                           # Data Transfer Objects
│   │   └── exception/                     # Manejo de excepciones global
│   └── account/
│       └── domain/                        # Agregado: Cuentas y Transacciones
│           ├── Transaction.java           # Entidad de transacción
│           ├── TransactionType.java       # Enum: TRANSFER, DEPOSIT, WITHDRAWAL
│           └── TransactionStatus.java     # Enum: PENDING, COMPLETED, FAILED, REVERSED
├── src/main/resources/static/             # Dashboard Frontend
│   ├── index.html                         # UI principal
│   ├── css/dashboard.css                  # Estilos
│   └── js/app.js                          # Lógica JavaScript
├── src/test/java/                         # Tests unitarios e integración
├── docs/adr/                              # Architecture Decision Records
├── docker-compose.yml                     # Composición de servicios
├── pom.xml                                # Dependencias Maven
├── Dockerfile                             # Imagen Docker de la app
├── DASHBOARD.md                           # Documentación del Dashboard
└── README.md                              # Este archivo
```

---

## 📄 Documentación

### Architecture Decision Records (ADRs)

Decisiones arquitectónicas detalladas y justificadas en `docs/adr`:

- **[ADR-001: Domain-Driven Design](docs/adr/ADR-001-domain-driven-design.md)** — Justificación de la arquitectura DDD, estructura de agregados y separación de responsabilidades
- **[ADR-002: Aislamiento y Concurrencia en Transacciones](docs/adr/ADR-002-transaction-isolation.md)** — Implementación de Pessimistic Locking para garantizar integridad ACID en operaciones concurrentes
- **[ADR-003: Estrategia de Manejo de Errores](docs/adr/ADR-003-error-handling.md)** — Manejo centralizado de excepciones, mapeo a HTTP status codes y auditoría de errores

### Protección de Ramas

- **`main`** — Protegida: Requiere PR aprobado + tests pasando + Code review de CODEOWNERS
- **`develop`** — Requiere PR aprobado
- Ver detalles de protección en [.github/CODEOWNERS](.github/CODEOWNERS)

### Releases & Versionado

Versión actual: **v0.4.0** (Todas las fases completadas: DDD, API REST, Tests, Swagger, Dashboard Admin)

Ver todas en [Releases](https://github.com/cozakoo/MicroBank_Core_Banking_Simulator/releases)

### Consumir MicroBank como Dependencia

MicroBank se publica automáticamente en GitHub Packages. Para usar en otro proyecto:

**1. Configurar `~/.m2/settings.xml`:**
```xml
<servers>
  <server>
    <id>github</id>
    <username>tu_usuario_github</username>
    <password>tu_github_token_personal</password>
  </server>
</servers>

<profiles>
  <profile>
    <id>github</id>
    <repositories>
      <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/cozakoo/MicroBank_Core_Banking_Simulator</url>
      </repository>
    </repositories>
  </profile>
</profiles>
```

**2. Agregar a tu `pom.xml`:**
```xml
<dependency>
    <groupId>com.microbank</groupId>
    <artifactId>microbank</artifactId>
    <version>0.4.0</version>
</dependency>
```

**3. Instalar:**
```bash
mvn clean install
```

---

## 🔧 Comandos Útiles

```bash
# Ver logs del contenedor en tiempo real
docker-compose logs -f microbank-app

# Ejecutar comandos en la BD
docker-compose exec postgres psql -U postgres -d microbank_db

# Limpiar todo (contenedores, volúmenes, redes)
docker-compose down -v

# Reconstruir sin caché
docker-compose build --no-cache

# Ver estado de los servicios
docker-compose ps
```

---

## 📊 Estado del Proyecto

### Versión Actual: v0.4.0 — ✅ COMPLETADO

```
┌────────────────────────────────────────────────────────────┐
│              FASES COMPLETADAS: 4 de 4                     │
├────────────────────────────────────────────────────────────┤
│ ✅ Fase 1: Setup & Dominio            [COMPLETADA]        │
│    - REQ-001 a REQ-004: 4/4            [100%]             │
│                                                            │
│ ✅ Fase 2: Lógica & Transacciones     [COMPLETADA]        │
│    - REQ-005 a REQ-009: 5/5            [100%]             │
│                                                            │
│ ✅ Fase 3: REST API & Tests            [COMPLETADA]       │
│    - REQ-010 a REQ-017: 8/8            [100%]             │
│                                                            │
│ ✅ Fase 4: Infraestructura & Extras   [COMPLETADA]        │
│    - REQ-018 a REQ-025: 8/8            [100%]             │
├────────────────────────────────────────────────────────────┤
│ TOTAL: 25/25 Requerimientos            [100%] ✅          │
│ Tests: 50+ pasando                     [100%]             │
│ Cobertura: >80%                        [VERIFICADA]       │
│ Build: SUCCESS                         [✓]                │
│ Documentación API: Swagger/OpenAPI     [✓]                │
│ Dashboard Admin: Funcional              [✓]                │
│ Estado: PRODUCCIÓN LISTA               [✓]                │
└────────────────────────────────────────────────────────────┘
```

### Componentes Implementados

| Componente | Estado | Descripción |
|---|---|---|
| **Entidades de Dominio** | ✅ | Account, Transaction, AuditLog |
| **Servicios de Negocio** | ✅ | AccountService, TransferService, DepositWithdrawService, AuditService |
| **Persistencia (JPA)** | ✅ | Repositories con índices optimizados |
| **Transacciones ACID** | ✅ | Locking pesimista, aislamiento READ_COMMITTED |
| **Auditoría** | ✅ | Registro automático de todas las operaciones |
| **Tests Unitarios** | ✅ | 50+ tests (Mockito, TestContainers, JUnit 5) |
| **Docker** | ✅ | docker-compose.yml, Dockerfile, servicios completos |
| **API REST Completa** | ✅ | 4 Controllers + 13 endpoints + Request/Response DTOs |
| **Documentación API (Swagger/OpenAPI)** | ✅ | Swagger UI + OpenAPI JSON + Ejemplos interactivos |
| **Dashboard Admin** | ✅ | UI moderna, depósitos, retiros, transferencias, búsqueda |
| **CI/CD** | ✅ | GitHub Actions pipeline configurado |

---

## 🗺 Roadmap Detallado

### Fase 1 — Setup & Dominio ✅
- [x] REQ-001: Setup Spring Boot + PostgreSQL + Docker
- [x] REQ-002: Entidad Account
- [x] REQ-003: Entidad Transaction
- [x] REQ-004: AccountRepository

### Fase 2 — Lógica de Negocio & Transacciones ✅
- [x] REQ-005: AccountService (Application Layer)
- [x] REQ-006: TransferService (Transferencias ACID)
- [x] REQ-007: DepositWithdrawService (Depósitos/Retiros)
- [x] REQ-008: AuditLog Entity (Auditoría)
- [x] REQ-009: AuditService (Registro automático)

### Fase 3 — REST API & Tests ✅
- [x] REQ-010: REST API - AccountController
- [x] REQ-011: REST API - TransferController
- [x] REQ-012: REST API - DepositWithdrawController
- [x] REQ-013: REST API - AuditController (Admin)
- [x] REQ-014: TransactionConfig
- [x] REQ-015: Tests Unitarios (Cobertura >80%)
- [x] REQ-016: Tests de Integración (TestContainers)
- [x] REQ-017: Documentación API (Swagger/OpenAPI)

### Fase 4 — Infraestructura & Extras ✅
- [x] REQ-018: Docker & docker-compose
- [x] REQ-019: CI/CD - GitHub Actions
- [x] REQ-020: Documentación de Decisiones Arquitectónicas (ADR)
- [x] REQ-021: InputValidator
- [x] REQ-022: Response Wrapper
- [x] REQ-023: Flyway Migrations
- [x] REQ-024: Spring Security
- [x] REQ-025: Dashboard Admin

---

## ❌ Solucionar Problemas

| Problema | Solución |
|---|---|
| `Connection refused` (ejecución local) | Verifica que PostgreSQL está corriendo: `pg_isready` |
| `Port 8080 already in use` | Mata el proceso: `lsof -i :8080` → `kill -9 <PID>` |
| `Port 5432 already in use` | PostgreSQL local está corriendo; usa opción 2 o detén el servicio |
| Docker no inicia | Verifica: `docker --version` y reinicia Docker Desktop |
| Contenedor no se conecta a BD | Espera a healthcheck: `docker-compose logs postgres` |
| Tests fallan localmente | Asegúrate que PostgreSQL está activo y TestContainers instalado |
| Dashboard no carga | Verifica que la app esté en `http://localhost:8080` |

---

## 📜 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 👨‍💻 Créditos Adicionales

### Herramientas & Tecnologías
- Spring Boot Team — Framework extraordinario
- PostgreSQL — Base de datos confiable
- Docker — Containerización
- Bootstrap — UI Framework
- Swagger/OpenAPI — Documentación de API

### Inspiración
- Domain-Driven Design (Eric Evans)
- Microservices Architecture (Sam Newman)
- Clean Code (Robert C. Martin)

---

*Última actualización: 11 de abril, 2026 — Fase 4 completada: Dashboard Admin + Todas las funcionalidades (v0.4.0)*
*Proyecto: ✅ Completado y Listo para Producción*
