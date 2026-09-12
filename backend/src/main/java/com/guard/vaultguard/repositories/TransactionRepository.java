package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByTransactionReference(String transactionReference);

    Optional<Transaction> findById(@NonNull UUID id);

    /**
     * Counts transactions whose associated RiskManagement row carries the given status.
     * Used by the concurrency integration test to assert that zero transactions remain
     * in PENDING state after the Kafka consumer has finished scoring everything.
     */
    @Query("SELECT COUNT(t) FROM transactions t JOIN t.riskManagement rm WHERE rm.transactionStatus = :status")
    long countGetTransactionsByTransactionStatus(@Param("status") TransactionStatus status);
}
