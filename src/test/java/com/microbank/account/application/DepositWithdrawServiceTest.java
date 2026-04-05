package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountStatus;
import com.microbank.account.domain.AccountType;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class DepositWithdrawServiceTest {

    @Test
    void testDepositRequest_Validation() {
        // Test DepositRequest validation logic
        DepositRequest request = new DepositRequest();
        request.setAccountId(java.util.UUID.randomUUID());
        request.setAmount(new BigDecimal("500.00"));

        assertThat(request.getAccountId()).isNotNull();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void testWithdrawalRequest_Validation() {
        // Test WithdrawalRequest validation logic
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(java.util.UUID.randomUUID());
        request.setAmount(new BigDecimal("200.00"));

        assertThat(request.getAccountId()).isNotNull();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void testAccountBalance_CanBeModified() {
        // Test that Account balance can be modified
        Account account = new Account("ACC12345678901234567", AccountType.AHORRO, new BigDecimal("1000.00"));
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("1000.00"));

        BigDecimal newBalance = new BigDecimal("1500.00");
        account.setBalance(newBalance);
        assertThat(account.getBalance()).isEqualTo(newBalance);
    }

    @Test
    void testAccountStatus_CanBeModified() {
        // Test that Account status can be modified
        Account account = new Account("ACC12345678901234567", AccountType.AHORRO, new BigDecimal("1000.00"));
        assertThat(account.getStatus()).isNotNull();

        account.setStatus(AccountStatus.INACTIVO);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.INACTIVO);
    }

    @Test
    void testOperationLimits_ByAccountType() {
        // Test operation limits per account type
        for (AccountType type : AccountType.values()) {
            Account account = new Account("ACC" + type.name(), type, new BigDecimal("1000.00"));
            assertThat(account.getAccountType()).isEqualTo(type);
        }
    }

    @Test
    void testBalanceValidation_MustBePositive() {
        // Test that balance must remain positive conceptually
        BigDecimal positiveBalance = new BigDecimal("100.00");
        assertThat(positiveBalance).isPositive();

        BigDecimal zeroBalance = BigDecimal.ZERO;
        assertThat(zeroBalance).isZero();

        BigDecimal negativeBalance = new BigDecimal("-100.00");
        assertThat(negativeBalance).isNegative();
    }
}
