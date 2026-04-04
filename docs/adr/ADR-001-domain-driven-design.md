# ADR-001: Domain-Driven Design (DDD)

**Estado:** Aceptado
**Fecha:** Abril 2026
**Autor:** Martín Arcos Vargas

## Contexto

MicroBank es un simulador de banca central que requiere manejar lógica de negocio compleja, especialmente en operaciones financieras críticas como transferencias, depósitos y retiros. La aplicación debe garantizar integridad de datos, auditoría completa y procesamiento robusto de transacciones concurrentes.

## Decisión

Se implementará **Domain-Driven Design (DDD)** como patrón arquitectónico principal del proyecto. Esto implica:

1. **Separación clara de responsabilidades** por agregados de dominio (`account/`, `transfer/`, `audit/`)
2. **Lógica de negocio pura** en el dominio, sin dependencias de Spring o persistencia
3. **Repositories** como abstracción para acceso a datos
4. **Servicios de dominio** que orquestan operaciones complejas
5. **Value Objects** para conceptos como `TransactionType`, `TransactionStatus`
6. **Excepciones de dominio** propias para cada contexto

## Justificación

- **Escalabilidad:** Nuevos agregados se agregan sin modificar código existente (Open/Closed Principle)
- **Testing:** Lógica de dominio se testea sin necesidad de BD o Spring
- **Mantenibilidad:** El código refleja el lenguaje del negocio (Ubiquitous Language)
- **Cumplimiento regulatorio:** Trazabilidad clara de cambios por agregado

## Consecuencias

### Positivas
- Código expresivo que refleja reglas de negocio
- Fácil de testear (tests unitarios rápidos sin infraestructura)
- Arquitectura escalable para múltiples dominios

### Negativas
- Requiere mapeo entre DTOs y entidades de dominio (boilerplate inicial)
- Curva de aprendizaje para nuevos desarrolladores sin experiencia en DDD

## Estructura Implementada

```
account/domain/
├── Transaction.java            # Entidad de dominio
├── TransactionType.java        # Value Object (Enum)
├── TransactionStatus.java      # Value Object (Enum)
├── AccountRepository.java      # Interfaz de repositorio
├── TransactionService.java     # Servicio de dominio (futuro)
└── exceptions/
    ├── InsufficientFundsException.java
    └── InvalidTransactionException.java
```

## Referencias

- Eric Evans, "Domain-Driven Design: Tackling Complexity in the Heart of Software" (2003)
- Vaughn Vernon, "Implementing Domain-Driven Design" (2013)
