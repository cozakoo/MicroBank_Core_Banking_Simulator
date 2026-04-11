package com.microbank.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * CONFIGURACIÓN DE TRANSACCIONES CRÍTICA (Martín)
 *
 * setDefaultTimeout(30) = 30 segundos
 * Por qué:
 * - Una transferencia con lock pesimista NO debería demorar más de 30s
 * - Si demora más, probable:
 *   1. Deadlock circular (evitado con ordenamiento UUID, pero redundancia es buena)
 *   2. BD colgada
 *   3. Código ineficiente
 * - Timeout actúa como circuit-breaker: evita que threads se queden esperando forever
 * - Lanza AccountLockedException → 409 Conflict al cliente
 *
 * @EnableTransactionManagement: Activa @Transactional en métodos
 * JpaTransactionManager: Usa transacciones de BD nativas (más confiable que Hibernate)
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        // ⚠️ Timeout crítico: Si una transacción tarda más de 30s, se aborta automáticamente
        transactionManager.setDefaultTimeout(30);
        return transactionManager;
    }
}
