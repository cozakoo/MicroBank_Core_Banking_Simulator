# MicroBank Core Banking Simulator

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%2B%20Mockito-25A162?style=flat-square)](https://junit.org/junit5/)
[![Coverage](https://img.shields.io/badge/Coverage->80%25-4CAF50?style=flat-square)](https://www.jacoco.org/)
[![Licencia](https://img.shields.io/badge/Licencia-MIT-blue?style=flat-square)](LICENSE)

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
| **Base de Datos** | PostgreSQL 15 |
| **Containerización** | Docker / Docker Compose |
| **Documentación API** | Springdoc-OpenAPI (Swagger) |
| **Testing** | JUnit 5, Mockito, TestContainers |
| **CI/CD** | GitHub Actions |

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
│   │   └── exception/                     # Manejo de excepciones
│   ├── domain/
│   │   ├── account/                       # Agregado: Cuentas
│   │   ├── transfer/                      # Agregado: Transferencias
│   │   └── audit/                         # Agregado: Auditoría
│   ├── infrastructure/                    # Implementaciones JPA
│   └── config/                            # Configuración de Spring
├── src/main/resources/
│   ├── application.yml                    # Config principal
│   ├── application-dev.yml                # Config desarrollo
│   └── application-prod.yml               # Config producción
├── src/test/java/                         # Tests unitarios e integración
├── docs/adr/                              # Architecture Decision Records
├── docker-compose.yml                     # Composición de servicios
├── pom.xml                                # Dependencias Maven
├── Dockerfile                             # Imagen Docker de la app
├── .gitignore                             # Archivos ignorados en Git
├── LICENSE                                # Licencia MIT
└── README.md                              # Este archivo
```

---

## 📄 Documentación

Decisiones arquitectónicas detalladas se encuentran en la carpeta `docs/adr`:

- [ADR-001: Domain-Driven Design](docs/adr/ADR-001-domain-driven-design.md)
- [ADR-002: Aislamiento de Transacciones](docs/adr/ADR-002-transaction-isolation.md)
- [ADR-003: Estrategia de Manejo de Errores](docs/adr/ADR-003-error-handling.md)

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

Las contribuciones son bienvenidas. Por favor revisa nuestra [Guía de Contribución](.github/CONTRIBUTING.md) para más detalles.

### Workflow para Contribuir

1. Fork el repositorio
2. Crea una rama (`git checkout -b feature/nueva-feature`)
3. Commits con mensaje descriptivo en español
4. Asegúrate que los tests pasen (`mvn test`)
5. Push a la rama (`git push origin feature/nueva-feature`)
6. Abre un Pull Request

---

## 📜 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 👨‍💻 Autor

**Martín Arcos Vargas**
- 📧 Email: martinarcosvargas2@gmail.com
- 🔗 GitHub: [@cozakoo](https://github.com/cozakoo)
- 🔗 LinkedIn: [martin-arcos](https://linkedin.com/in/martin-arcos)

---

*Última actualización: 3 de abril, 2026*
