package com.microbank.account.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String nroCuenta);

    List<Account> findByStatus(AccountStatus estado);

    boolean existsByAccountNumber(String nroCuenta);
}
