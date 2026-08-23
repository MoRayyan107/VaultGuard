package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM transactions t JOIN FETCH t.riskManagement rm WHERE rm.riskScore >= :scoreThreshold")
    List<Transaction> findByRiskScoreGreaterThan(@Param("scoreThreshold") double riskScoreThreshold);

    @Query("SELECT t FROM transactions t JOIN FETCH t.riskManagement rm WHERE rm.transactionStatus = :status")
    List<Transaction> findByTransactionStatus(@Param("status") TransactionStatus status);

    Optional<Transaction> findByTransactionReference(String transactionReference);

    Optional<Transaction> findById(@NonNull UUID id);

}
