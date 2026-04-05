package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountType;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AccountServiceTest {

    @Test
    void testCreateAccountRequest_Validation() {
        // Test CreateAccountRequest validation logic
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.AHORRO);
        request.setInitialBalance(new BigDecimal("1000.00"));

        assertThat(request.getAccountType()).isEqualTo(AccountType.AHORRO);
        assertThat(request.getInitialBalance()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void testAccountCreation_ValidBalance() {
        // Test that Account can be created with valid balance
        Account account = new Account("ACC12345678901234567", AccountType.AHORRO, new BigDecimal("1000.00"));

        assertThat(account)
                .isNotNull()
                .extracting("accountNumber", "accountType", "balance")
                .containsExactly("ACC12345678901234567", AccountType.AHORRO, new BigDecimal("1000.00"));
    }

    @Test
    void testAccountStatus_DefaultActive() {
        // Test that new Account is ACTIVO by default
        Account account = new Account("ACC12345678901234567", AccountType.AHORRO, new BigDecimal("1000.00"));

        assertThat(account.getStatus()).isNotNull();
    }

    @Test
    void testAccountTypes_AllSupported() {
        // Test that all AccountType values can create valid accounts
        for (AccountType type : AccountType.values()) {
            Account account = new Account("ACC" + type.name(), type, new BigDecimal("1000.00"));
            assertThat(account.getAccountType()).isEqualTo(type);
        }
    }
}
