# ADR-002: Aislamiento y Concurrencia en Transacciones

**Estado:** Aceptado
**Fecha:** Abril 2026
**Autor:** Martín Arcos Vargas

## Contexto

MicroBank procesa transferencias financieras de forma concurrente. Sin mecanismos adecuados de aislamiento, pueden ocurrir race conditions que comprometan la integridad de datos:

- **Dirty Read:** Una transacción lee datos modificados pero no confirmados
- **Non-Repeatable Read:** Datos leídos en la misma transacción cambian entre lecturas
- **Phantom Read:** Nuevas filas aparecen entre lecturas de la misma transacción

Ejemplo de race condition sin control:

```
Thread A: lee balance de Cuenta X = $100
Thread B: lee balance de Cuenta X = $100
Thread A: transfiere $60, escribe balance = $40
Thread B: transfiere $50, escribe balance = $50  ← INCORRECTO (perdemos $60)
```

## Decisión

Se implementará **Pessimistic Locking** a nivel de base de datos usando JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)`:

### Estrategia

1. **Lock explícito en transacciones financieras críticas**
   ```java
   @Query("SELECT a FROM Account a WHERE a.id = :id")
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   Account findByIdForUpdate(UUID id);
   ```

2. **Orden consistente de locks** (evitar deadlock)
   ```java
   @Transactional
   public Transfer executeTransfer(UUID fromId, UUID toId, BigDecimal amount) {
       // Lock siempre en orden ascendente de IDs
       UUID first = fromId.compareTo(toId) < 0 ? fromId : toId;
       UUID second = fromId.compareTo(toId) < 0 ? toId : fromId;

       Account from = accountRepository.findByIdForUpdate(first);
       Account to = accountRepository.findByIdForUpdate(second);

       // Ahora están lockeadas y no hay deadlock
       if (from.getBalance().compareTo(amount) < 0) {
           throw new InsufficientFundsException(...);
       }

       from.debit(amount);
       to.credit(amount);
       // Commit libera los locks automáticamente
   }
   ```

3. **Nivel de aislamiento: READ_COMMITTED**
   Configurado en `application.yml` como defecto de PostgreSQL. Suficiente porque los locks pessimistas lo refuerzan.

## Alternativas Consideradas

### 1. Optimistic Locking (Descartada)
```java
@Version
private Long version;
```
**Razón:** En operaciones financieras, los retries indefinidos son aceptables pero pueden afectar UX. Pessimistic es más predecible.

### 2. Read_Uncommitted (Descartada)
**Razón:** Permite dirty reads, inaceptable en finanzas.

### 3. Serializable (Descartada)
**Razón:** Performance impacto enorme en aplicaciones con alta concurrencia. PESSIMISTIC_WRITE es suficiente.

## Implementación

### Duración del Lock
- **Mínima posible:** El lock se libera al final de la transacción `@Transactional`
- **No loops:** Nunca mantener locks en loops

```java
// ❌ MALO
@Transactional
public void processMultipleTransfers(List<Transfer> transfers) {
    for (Transfer t : transfers) {
        Account from = accountRepository.findByIdForUpdate(t.getFromId()); // Lock individual
        // ... procesamiento ...
    }
}

// ✅ BUENO
@Transactional
public void processTransfer(UUID fromId, UUID toId, BigDecimal amount) {
    Account from = accountRepository.findByIdForUpdate(fromId); // Lock una sola vez
    Account to = accountRepository.findByIdForUpdate(toId);
    // ... procesamiento ...
}
```

### Testing de Concurrencia

```java
@Test
void testConcurrentTransfersNoRaceCondition() throws InterruptedException {
    Account a = new Account();
    a.setBalance(BigDecimal.valueOf(100));
    accountRepository.save(a);

    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (int i = 0; i < 10; i++) {
        executor.submit(() -> {
            transferService.executeTransfer(a.getId(), b.getId(), BigDecimal.TEN);
        });
    }

    executor.awaitTermination(10, TimeUnit.SECONDS);

    // Verificar que el balance es correcto después de 10 transferencias de $10
    Account updated = accountRepository.findById(a.getId()).orElseThrow();
    assertEquals(BigDecimal.valueOf(0), updated.getBalance());
}
```

## Consecuencias

### Positivas
- **Garantías ACID:** Integridad de datos garantizada incluso con alta concurrencia
- **Predictibilidad:** Sin excepciones de versión (como en optimistic locking)
- **Sencillez:** Los locks son manejados por la BD automáticamente

### Negativas
- **Performance:** Locks pueden causar espera de threads
- **Deadlock potencial:** Si no se respeta el orden de locks (mitigado con fixture de orden consistente)
- **Escalabilidad limitada:** Con muy alto volumen, considerar sharding por agregado

## Monitoreo

En producción, monitorear:
```sql
SELECT * FROM pg_locks WHERE NOT granted;  -- Locks en espera
SELECT * FROM pg_stat_activity;             -- Transacciones activas y su duración
```

## Referencias

- PostgreSQL Docs: [Explicit Locking](https://www.postgresql.org/docs/15/explicit-locking.html)
- Hibernate Docs: [@Lock Annotation](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#locking)
- Java EE Spec: [LockModeType](https://jakarta.ee/specifications/persistence/3.1/apidocs/jakarta.persistence/lockmodetyp)
