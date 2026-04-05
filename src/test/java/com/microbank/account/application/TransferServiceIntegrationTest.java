package com.microbank.account.application;

import com.microbank.account.domain.Account;
import com.microbank.account.domain.AccountRepository;
import com.microbank.account.domain.AccountType;
import com.microbank.account.domain.TransactionRepository;
import com.microbank.account.domain.TransactionStatus;
import com.microbank.shared.exceptions.AccountNotFoundException;
import com.microbank.shared.exceptions.InsufficientFundsException;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID sourceId;
    private UUID targetId;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        Account sourceAccount = new Account("ACC1111111111111", AccountType.CORRIENTE, new BigDecimal("5000.00"));
        Account targetAccount = new Account("ACC2222222222222", AccountType.AHORRO, new BigDecimal("1000.00"));

        sourceAccount = accountRepository.save(sourceAccount);
        targetAccount = accountRepository.save(targetAccount);

        sourceId = sourceAccount.getId();
        targetId = targetAccount.getId();
    }

    @Test
    void transferFunds_Success_UpdatesBothAccounts() {
        TransferRequest request = new TransferRequest(sourceId, targetId, new BigDecimal("500.00"));
        var result = transferService.transferFunds(request);

        assertThat(result)
                .isNotNull()
                .extracting("status", "amount")
                .containsExactly(TransactionStatus.COMPLETADA, new BigDecimal("500.00"));

        Account source = accountRepository.findById(sourceId).orElseThrow();
        Account target = accountRepository.findById(targetId).orElseThrow();

        assertThat(source.getBalance()).isEqualTo(new BigDecimal("4500.00"));
        assertThat(target.getBalance()).isEqualTo(new BigDecimal("1500.00"));
    }

    @Test
    void transferFunds_SourceNoBalance_Rollbacks() {
        accountRepository.findById(sourceId).ifPresent(acc -> {
            acc.setBalance(new BigDecimal("100.00"));
            accountRepository.save(acc);
        });

        TransferRequest request = new TransferRequest(sourceId, targetId, new BigDecimal("500.00"));

        assertThatThrownBy(() -> transferService.transferFunds(request))
                .isInstanceOf(InsufficientFundsException.class);

        Account source = accountRepository.findById(sourceId).orElseThrow();
        Account target = accountRepository.findById(targetId).orElseThrow();

        assertThat(source.getBalance()).isEqualTo(new BigDecimal("100.00"));
        assertThat(target.getBalance()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void transferFunds_InvalidSourceAccount_ThrowsException() {
        TransferRequest request = new TransferRequest(UUID.randomUUID(), targetId, new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferService.transferFunds(request))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void transferFunds_InvalidTargetAccount_ThrowsException() {
        TransferRequest request = new TransferRequest(sourceId, UUID.randomUUID(), new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferService.transferFunds(request))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void transferFunds_SameSourceAndTarget_ThrowsException() {
        TransferRequest request = new TransferRequest(sourceId, sourceId, new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferService.transferFunds(request))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void transferFunds_ConcurrentRequests_NoDeadlock() throws InterruptedException {
        Account account3 = new Account("ACC3333333333333", AccountType.CORRIENTE, new BigDecimal("10000.00"));
        Account account4 = new Account("ACC4444444444444", AccountType.AHORRO, new BigDecimal("5000.00"));

        account3 = accountRepository.save(account3);
        account4 = accountRepository.save(account4);

        UUID id3 = account3.getId();
        UUID id4 = account4.getId();

        int numThreads = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    TransferRequest request = new TransferRequest(id3, id4, new BigDecimal("100.00"));
                    transferService.transferFunds(request);
                    successCount.incrementAndGet();
                } catch (InsufficientFundsException | InvalidAccountException e) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        assertThat(successCount.get() + failureCount.get()).isEqualTo(numThreads);
        assertThat(successCount.get()).isGreaterThan(0);

        Account finalSource = accountRepository.findById(id3).orElseThrow();
        Account finalTarget = accountRepository.findById(id4).orElseThrow();

        BigDecimal totalBalance = finalSource.getBalance().add(finalTarget.getBalance());
        assertThat(totalBalance).isEqualTo(new BigDecimal("15000.00"));
    }

    @Test
    void transferFunds_ACIDProperties_RollbackOnFailure() {
        accountRepository.findById(sourceId).ifPresent(acc -> {
            acc.setBalance(new BigDecimal("100.00"));
            accountRepository.save(acc);
        });

        TransferRequest request = new TransferRequest(sourceId, targetId, new BigDecimal("500.00"));

        assertThatThrownBy(() -> transferService.transferFunds(request))
                .isInstanceOf(InsufficientFundsException.class);

        Account source = accountRepository.findById(sourceId).orElseThrow();
        Account target = accountRepository.findById(targetId).orElseThrow();

        assertThat(source.getBalance()).isEqualTo(new BigDecimal("100.00"));
        assertThat(target.getBalance()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void transferFunds_MultipleSequentialTransfers_Success() {
        for (int i = 0; i < 3; i++) {
            TransferRequest request = new TransferRequest(sourceId, targetId, new BigDecimal("100.00"));
            transferService.transferFunds(request);
        }

        Account source = accountRepository.findById(sourceId).orElseThrow();
        Account target = accountRepository.findById(targetId).orElseThrow();

        assertThat(source.getBalance()).isEqualTo(new BigDecimal("4700.00"));
        assertThat(target.getBalance()).isEqualTo(new BigDecimal("1300.00"));

        long transactionCount = transactionRepository.count();
        assertThat(transactionCount).isEqualTo(3);
    }
}
