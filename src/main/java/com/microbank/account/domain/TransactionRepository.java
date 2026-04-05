package com.microbank.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findBySourceAccountId(UUID sourceAccountId);

    List<Transaction> findByTargetAccountId(UUID targetAccountId);

    List<Transaction> findByStatus(TransactionStatus status);
}
