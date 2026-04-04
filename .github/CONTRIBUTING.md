# Guía de Contribución — MicroBank Core Banking Simulator

¡Gracias por tu interés en contribuir a MicroBank! Este documento describe el proceso de contribución, convenciones de código y estándares de calidad.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Workflow de Contribución](#workflow-de-contribución)
3. [Convenciones de Git](#convenciones-de-git)
4. [Estándares de Código](#estándares-de-código)
5. [Testing](#testing)
6. [Pull Requests](#pull-requests)

---

## Requisitos Previos

### Herramientas
- **Java 17+** — [Descargar Eclipse Temurin](https://adoptium.net/)
- **Maven 3.8+** — Verificar con `mvn -version`
- **PostgreSQL 15** — Para ejecución local
- **Git** — Control de versiones
- **Docker & Docker Compose** — Opcional pero recomendado

### Setup Inicial
```bash
# 1. Clonar el repositorio
git clone https://github.com/cozakoo/MicroBank_Core_Banking_Simulator.git
cd MicroBank_Core_Banking_Simulator

# 2. Crear rama local
git checkout -b sprint1/req_004_account_repository

# 3. Instalar dependencias
mvn clean install

# 4. Ejecutar tests locales
mvn test
```

---

## Workflow de Contribución

### 1️⃣ Crear una Rama

**Formato:** `sprint<nro>/req_00<nro>_<nombre_requerimiento>`

```bash
git checkout develop
git pull origin develop
git checkout -b sprint1/req_002_entidad_account
```

### 2️⃣ Trabajar y Testear

```bash
mvn clean test
mvn clean install
```

### 3️⃣ Pushear y Crear PR

```bash
git push -u origin sprint1/req_002_entidad_account
```

En GitHub:
- Base: `develop`
- Título: Descripción breve del REQ
- Descripción: Qué cambia, cómo testear

---

## Convenciones de Git

### Commits — Conventional Commits

```
tipo(scope): descripción corta

- Cambio 1
- Cambio 2

Fixes #123
```

**Tipos:** `feat`, `fix`, `refactor`, `test`, `docs`, `chore`

**Ejemplo:**
```
feat(transfer): implementar TransferService con pessimistic locking

Fixes #42
```

---

## Estándares de Código

### Imports
```
Java/Jakarta → Spring → Locales
(alfabético en cada grupo)
```

### Naming
- Clases: `PascalCase`
- Métodos: `camelCase`
- Constantes: `SCREAMING_SNAKE_CASE`

### Reglas Hard
- ✅ **BigDecimal** para dinero
- ✅ **`@Transactional`** en métodos que modifican datos
- ✅ **Excepciones propias** de dominio
- ✅ Sin código comentado

---

## Testing

```bash
mvn test                          # Todos los tests
mvn test jacoco:report            # Con cobertura
```

**Requisitos:**
- Coverage en dominio > 80%
- Patrón AAA: Arrange-Act-Assert

---

## Pull Requests

### Checklist
- [ ] Rama desde `develop`
- [ ] Tests pasan
- [ ] Build completo (`mvn clean install`)
- [ ] Documentación actualizada

---

## Contacto

- **Martín Arcos Vargas** — [@cozakoo](https://github.com/cozakoo)
- **Lucas** — [@Lkss01](https://github.com/Lkss01)

---

**Última actualización:** Abril 2026
