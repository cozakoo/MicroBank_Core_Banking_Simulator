package com.microbank.shared.validation;

import com.microbank.shared.exceptions.InvalidAccountException;
import com.microbank.shared.exceptions.InvalidAmountException;
import com.microbank.shared.exceptions.InvalidEmailException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class InputValidator {

    // Pattern: 10 caracteres, solo mayúsculas y números
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[A-Z0-9]{10}$");

    // Pattern RFC 5322 simplificado para emails
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Constructor privado para evitar que se instancie esta clase utilitaria
    private InputValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || !ACCOUNT_PATTERN.matcher(accountNumber).matches()) {
            throw new InvalidAccountException("El número de cuenta es inválido. Debe tener 10 caracteres alfanuméricos en mayúscula.");
        }
    }

    public static void validatePositiveAmount(BigDecimal amount) {
        // Validamos que no sea nulo, que sea mayor a 0, y que la escala (decimales) no sea mayor a 2
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.scale() > 2) {
            throw new InvalidAmountException("El monto debe ser positivo y tener como máximo 2 decimales.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("El formato del correo electrónico es inválido.");
        }
    }
}