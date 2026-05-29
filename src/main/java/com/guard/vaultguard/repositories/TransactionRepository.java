package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByRiskScoreGreaterThan(double riskScoreIsGreaterThan);

    List<Transaction> findByTransactionStatus(TransactionStatus status);

    Optional<Transaction> findById(Long id);
}
