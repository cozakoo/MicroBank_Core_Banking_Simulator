package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountStatus;
import com.microbank.audit.application.AuditService;
import com.microbank.audit.domain.AuditAction;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microbank.auth.domain.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea una nueva cuenta bancaria.
     * Genera automáticamente un número de cuenta único.
     * @param request DTO con tipo de cuenta y balance inicial
     * @return Account creada
     * @throws InvalidAccountException si el balance es inválido
     */
    @Transactional
    public Account createAccount(CreateAccountRequest request, UUID ownerId) {
        log.info("Creando nueva cuenta de tipo: {} para owner: {}", request.getAccountType(), ownerId);

        if (request.getInitialBalance() == null || request.getInitialBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAccountException("El balance inicial debe ser mayor a cero");
        }

        String accountNumber = generateUniqueAccountNumber();

        Account account = new Account(
                accountNumber,
                request.getAccountType(),
                request.getInitialBalance(),
                ownerId
        );

        // Guardar en BD
        Account savedAccount = accountRepository.save(account);
        log.info("Cuenta creada exitosamente. ID: {}, Número: {}", savedAccount.getId(), savedAccount.getAccountNumber());

        // Registrar en auditoría
        String auditDetails = String.format("Cuenta creada - Tipo: %s, Balance inicial: %s",
                request.getAccountType(), request.getInitialBalance());
        auditService.logAction(savedAccount.getId(), AuditAction.CREAR_CUENTA, auditDetails, null);

        return savedAccount;
    }

    /**
     * Obtiene una cuenta por su ID.
     * @param id UUID de la cuenta
     * @return Account
     * @throws AccountNotFoundException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public Account getAccountById(UUID id) {
        log.debug("Buscando cuenta con ID: {}", id);

        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cuenta no encontrada. ID: {}", id);
                    return new AccountNotFoundException("Cuenta no encontrada con ID: " + id);
                });
    }

    /**
     * Obtiene una cuenta por su número.
     * @param accountNumber número de cuenta
     * @return Account
     * @throws AccountNotFoundException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        log.debug("Buscando cuenta con número: {}", accountNumber);

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn("Cuenta no encontrada. Número: {}", accountNumber);
                    return new AccountNotFoundException("Cuenta no encontrada con número: " + accountNumber);
                });
    }

    /**
     * Obtiene todas las cuentas (solo admin).
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        log.debug("Obteniendo todas las cuentas");
        return accountRepository.findAll();
    }

    /**
     * Obtiene las cuentas del usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByOwner(String username) {
        UUID ownerId = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidAccountException("Usuario no encontrado: " + username))
                .getId();
        log.debug("Obteniendo cuentas del usuario: {}", username);
        return accountRepository.findByOwnerId(ownerId);
    }

    /**
     * Actualiza el estado de una cuenta.
     * @param id UUID de la cuenta
     * @param newStatus nuevo estado
     * @return Account actualizada
     * @throws AccountNotFoundException si la cuenta no existe
     * @throws InvalidAccountException si el estado es inválido
     */
    @Transactional
    public Account updateAccountStatus(UUID id, AccountStatus newStatus) {
        log.info("Actualizando estado de cuenta. ID: {}, Nuevo estado: {}", id, newStatus);

        if (newStatus == null) {
            throw new InvalidAccountException("El estado de la cuenta no puede ser nulo");
        }

        Account account = getAccountById(id);
        account.setStatus(newStatus);
        Account updated = accountRepository.save(account);

        log.info("Estado de cuenta actualizado. ID: {}, Nuevo estado: {}", id, newStatus);
        return updated;
    }

    /**
     * Asigna o actualiza el alias de una cuenta.
     * El alias debe ser único entre todas las cuentas.
     */
    @Transactional
    public Account setAlias(UUID accountId, String alias) {
        if (accountRepository.existsByAlias(alias)) {
            Account existing = accountRepository.findByAlias(alias).get();
            if (!existing.getId().equals(accountId)) {
                throw new InvalidAccountException("El alias '" + alias + "' ya está en uso por otra cuenta");
            }
        }
        Account account = getAccountById(accountId);
        account.setAlias(alias);
        return accountRepository.save(account);
    }

    /**
     * Busca una cuenta por número de cuenta o alias.
     * Útil para transferencias sin conocer el UUID.
     */
    @Transactional(readOnly = true)
    public Account getAccountByNumberOrAlias(String identifier) {
        // Intentar por número de cuenta primero
        return accountRepository.findByAccountNumber(identifier)
                .or(() -> accountRepository.findByAlias(identifier))
                .orElseThrow(() -> new AccountNotFoundException(
                        "Cuenta no encontrada con número o alias: " + identifier));
    }

    /**
     * Genera un número de cuenta único.
     * Formato: ACC + primeros 17 caracteres de UUID sin guiones
     * @return número de cuenta único
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            if (attempts > 0) {
                log.warn("Número de cuenta ya existe, generando uno nuevo. Intento: {}", attempts);
            }

            // Generar número de cuenta: ACC + UUID sin guiones (primeros 17 chars)
            String uuidWithoutHyphens = UUID.randomUUID().toString().replace("-", "");
            accountNumber = "ACC" + uuidWithoutHyphens.substring(0, 17);
            attempts++;

        } while (accountRepository.existsByAccountNumber(accountNumber) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            log.error("No se pudo generar un número de cuenta único después de {} intentos", maxAttempts);
            throw new InvalidAccountException("No se pudo generar un número de cuenta único");
        }

        log.debug("Número de cuenta generado: {}", accountNumber);
        return accountNumber;
    }
}
