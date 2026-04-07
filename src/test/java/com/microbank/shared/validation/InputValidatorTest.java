package com.microbank.shared.validation;

import com.microbank.shared.exceptions.InvalidAccountException;
import com.microbank.shared.exceptions.InvalidAmountException;
import com.microbank.shared.exceptions.InvalidEmailException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void validateAccountNumber_Success() {
        assertDoesNotThrow(() -> InputValidator.validateAccountNumber("ACC0012345"));
    }

    @Test
    void validateAccountNumber_NullOrInvalid_ThrowsException() {
        assertThrows(InvalidAccountException.class, () -> InputValidator.validateAccountNumber(null));
        assertThrows(InvalidAccountException.class, () -> InputValidator.validateAccountNumber("acc123")); // Minúsculas y corto
    }

    @Test
    void validatePositiveAmount_Success() {
        assertDoesNotThrow(() -> InputValidator.validatePositiveAmount(new BigDecimal("100.50")));
    }

    @Test
    void validatePositiveAmount_Invalid_ThrowsException() {
        assertThrows(InvalidAmountException.class, () -> InputValidator.validatePositiveAmount(null));
        assertThrows(InvalidAmountException.class, () -> InputValidator.validatePositiveAmount(new BigDecimal("-50.00"))); // Negativo
        assertThrows(InvalidAmountException.class, () -> InputValidator.validatePositiveAmount(BigDecimal.ZERO)); // Cero
    }

    @Test
    void validateEmail_Success() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("usuario@microbank.com"));
    }

    @Test
    void validateEmail_Invalid_ThrowsException() {
        assertThrows(InvalidEmailException.class, () -> InputValidator.validateEmail("usuario-sin-arroba.com"));
        assertThrows(InvalidEmailException.class, () -> InputValidator.validateEmail(null));
    }
}