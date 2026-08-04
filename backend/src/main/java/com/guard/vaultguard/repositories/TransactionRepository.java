package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM transactions t JOIN FETCH t.riskManagement rm WHERE rm.riskScore >= :scoreThreshold")
    List<Transaction> findByRiskScoreGreaterThan(@Param("scoreThreshold") double riskScoreThreshold);

    @Query("SELECT t FROM transactions t JOIN FETCH t.riskManagement rm WHERE rm.transactionStatus = :status")
    List<Transaction> findByTransactionStatus(@Param("status") TransactionStatus status);

    @Query("SELECT t FROM transactions t JOIN FETCH t.riskManagement rm WHERE t.senderBank.bankId = :bankId " +
            "OR t.recipientBank.bankId = :bankId")
    List<Transaction> findAllByBankId(@Param("bankId") UUID bankId);

    @Query("SELECT t FROM transactions t JOIN risk_management rm ON t.id = rm.transaction.id")
    List<Transaction> findAll();

    Optional<Transaction> findById(@NonNull UUID id);

}
