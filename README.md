# MicroBank Core Banking Simulator

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%2B%20Mockito-25A162?style=flat-square)](https://junit.org/junit5/)
[![Coverage](https://img.shields.io/badge/Coverage->80%25-4CAF50?style=flat-square)](https://www.jacoco.org/)
[![Licencia](https://img.shields.io/badge/Licencia-MIT-blue?style=flat-square)](LICENSE)

## 👥 Autores & Contribuidores

| Rol | Persona | GitHub |
|---|---|---|
| **👤 Autor Principal** | Martín Arcos Vargas | [@cozakoo](https://github.com/cozakoo) |
| **🤝 Co-Desarrollador** | Lucas | [@Lkss01](https://github.com/Lkss01) |

## 🏦 Descripción del Proyecto

MicroBank es un simulador de banca central de alto rendimiento diseñado para manejar operaciones financieras críticas con precisión absoluta. Este proyecto demuestra principios avanzados de ingeniería de software incluyendo **Domain-Driven Design (DDD)**, **integridad ACID** y **gestión robusta de concurrencia** mediante Pessimistic Locking a nivel de base de datos.

El simulador permite gestionar cuentas bancarias, ejecutar transferencias seguras entre cuentas y mantener un registro completo de auditoría de cada transacción, asegurando cumplimiento e integridad de datos en todo momento.

## 🎯 Objetivos Clave

- **Integridad ACID**: Garantizar que todas las operaciones financieras sean atómicas, consistentes, aisladas y duraderas.
- **Arquitectura DDD**: Implementación de separación clara de responsabilidades usando patrones de Domain-Driven Design.
- **Procesamiento Concurrente**: Manejo de race conditions en transferencias usando pessimistic locking a nivel de BD.
- **Auditoría Completa**: Cada operación genera un registro inmutable de auditoría para trazabilidad y cumplimiento normativo.
- **Experiencia del Desarrollador**: Entorno dockerizado con setup de un comando.
- **Excelencia en Testing**: Cobertura alta con tests unitarios, de integración (TestContainers) y estrés.
- **API RESTful**: API limpia y documentada con OpenAPI/Swagger.
- **CI/CD Listo**: Pipelines automatizados para testing, análisis de calidad y containerización.

## 🏗 Arquitectura

El proyecto sigue un enfoque de **Domain-Driven Design (DDD)** para manejar la complejidad:

```
┌─────────────────────────────────────────────────────────┐
│             REST API Controllers                        │
│  (AccountController, TransferController)               │
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
- 📊 **pgAdmin** (Gestión de BD): http://localhost:5050
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
| **Testing** | JUnit 5, Mockito, H2 (tests), TestContainers (futuros) |
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
│           ├── Transaction.java           # Entidad de transacción (REQ-003)
│           ├── TransactionType.java       # Enum: TRANSFER, DEPOSIT, WITHDRAWAL
│           └── TransactionStatus.java     # Enum: PENDING, COMPLETED, FAILED, REVERSED
├── src/main/resources/
│   ├── application.yml                    # Config principal
│   ├── application-dev.yml                # Config desarrollo
│   └── application-prod.yml               # Config producción
├── src/test/java/                         # Tests unitarios e integración
├── docs/adr/                              # Architecture Decision Records
├── docker-compose.yml                     # Composición de servicios
├── pom.xml                                # Dependencias Maven
├── Dockerfile                             # Imagen Docker de la app
└── README.md                              # Este archivo
```

---

## 📄 Documentación

### Architecture Decision Records (ADRs)

Decisiones arquitectónicas detalladas y justificadas en `docs/adr`:

- **[ADR-001: Domain-Driven Design](docs/adr/ADR-001-domain-driven-design.md)** — Justificación de la arquitectura DDD, estructura de agregados y separación de responsabilidades
- **[ADR-002: Aislamiento y Concurrencia en Transacciones](docs/adr/ADR-002-transaction-isolation.md)** — Implementación de Pessimistic Locking para garantizar integridad ACID en operaciones concurrentes
- **[ADR-003: Estrategia de Manejo de Errores](docs/adr/ADR-003-error-handling.md)** — Manejo centralizado de excepciones, mapeo a HTTP status codes y auditoría de errores

### Guía de Contribución

Revisa [.github/CONTRIBUTING.md](.github/CONTRIBUTING.md) para:
- Setup de desarrollo
- Workflow de contribución
- Convenciones de código y commits
- Estándares de testing

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

## ❌ Solucionar Problemas

| Problema | Solución |
|---|---|
| `Connection refused` (ejecución local) | Verifica que PostgreSQL está corriendo: `pg_isready` |
| `Port 8080 already in use` | Mata el proceso: `lsof -i :8080` → `kill -9 <PID>` |
| `Port 5432 already in use` | PostgreSQL local está corriendo; usa opción 2 o detén el servicio |
| Docker no inicia | Verifica: `docker --version` y reinicia Docker Desktop |
| Contenedor no se conecta a BD | Espera a healthcheck: `docker-compose logs postgres` |
| Tests fallan localmente | Asegúrate que PostgreSQL está activo y TestContainers instalado |

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor revisa nuestra **[Guía de Contribución](.github/CONTRIBUTING.md)** para más detalles sobre:
- Setup de desarrollo
- Formato de ramas: `sprint<nro>/req_00<nro>_<nombre_requerimiento>`
- Convenciones de commits: Conventional Commits
- Estándares de código: DDD, testing, naming
- Proceso de Pull Request

### Resumen Rápido

1. Crea rama desde `develop`: `git checkout -b sprint1/req_002_entidad_account`
2. Trabaja localmente y testea: `mvn test`
3. Commits con mensajes descriptivos en español
4. Push: `git push -u origin sprint1/req_002_entidad_account`
5. Abre PR contra `develop`

---

## 📜 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 👨‍💻 Créditos

### Autor Principal
**Martín Arcos Vargas** ([@cozakoo](https://github.com/cozakoo))
- 📧 Email: martinarcosvargas2@gmail.com
- 🔗 LinkedIn: [martin-arcos](https://linkedin.com/in/martin-arcos)
- 🌐 Portfolio: [arcosvargas.com](https://arcosvargas.com)

### Co-Desarrollador
**Lucas** ([@Lkss01](https://github.com/Lkss01))
- Contribuidor activo en arquitectura, testing y code review

---

*Última actualización: 4 de abril, 2026*
