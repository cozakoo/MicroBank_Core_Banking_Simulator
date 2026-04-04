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

**Formato obligatorio:** `sprint<nro>/req_00<nro>_<nombre_requerimiento>`

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
- **Base:** `develop` (nunca a `main` — está protegida)
- **Título:** `REQ-XXX: Descripción breve del requerimiento`
- **Descripción:**
  - Qué se implementó
  - Por qué se hizo así
  - Cómo testear
  - `Fixes #XX` (número del issue si aplica)

### 4️⃣ Code Review (CODEOWNERS)

- Los CODEOWNERS se auto-solicitan via [`.github/CODEOWNERS`](.github/CODEOWNERS)
- Esperá feedback de Martin (@cozakoo) o Lucas (@Lkss01)
- Responde comentarios y haz cambios solicitados
- Los nuevos commits se agregan automáticamente al PR

### 5️⃣ Mergeo a `develop`

Una vez aprobado y tests pasen (GitHub Actions):
- El maintainer mergea a `develop`
- **No mergear directamente** — usar UI de GitHub

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
- [ ] Tests pasan (`mvn test`)
- [ ] Build completo (`mvn clean install`)
- [ ] Commits con mensajes Conventional Commits
- [ ] Documentación actualizada si es necesario
- [ ] Sin código comentado

### Descripción Recomendada

```markdown
## Descripción
Breve explicación de qué se implementó.

## Cambios
- Cambio 1
- Cambio 2

## Cómo testear
1. Ejecutar `mvn test -Dtest=NombreTest`
2. Verificar que X funciona

## Relacionado
Fixes #42
```

---

## Releases

### Crear una Release (Solo Maintainers)

**1. Actualizar versión en `pom.xml`:**
```xml
<version>0.1.0</version>
```

**2. Commit y tag:**
```bash
git add pom.xml
git commit -m "chore: bump version to 0.1.0"
git tag -a v0.1.0 -m "Release v0.1.0 - descripción"
git push origin v0.1.0
```

**3. Compilar:**
```bash
mvn clean package -DskipTests
```

**4. En GitHub (crear release manual):**
- https://github.com/cozakoo/MicroBank_Core_Banking_Simulator/releases
- "Create a new release"
- Tag: `v0.1.0`
- Title: `v0.1.0 - Feature Description`
- Descripción: Features, bugs fixes, cambios
- Adjuntar JAR desde `target/microbank-0.1.0.jar`
- "Publish release"

**5. Mergear a `main` (protegida):**
```bash
git checkout main
git pull
git merge develop
git push origin main
```

---

## Ramas Protegidas

- **`main`** — Producción: Requiere PR aprobado + tests pasando + CODEOWNERS review
- **`develop`** — Integración: Requiere PR aprobado

Ver detalles en [.github/CODEOWNERS](.github/CODEOWNERS)

---

## Contacto

**Maintainers:**
- **Martín Arcos Vargas** — [@cozakoo](https://github.com/cozakoo)
- **Lucas** — [@Lkss01](https://github.com/Lkss01)

**CODEOWNERS (auto-request reviews):**
Ver [.github/CODEOWNERS](.github/CODEOWNERS) para saber quién revisa cada área

---

**Última actualización:** Abril 2026
