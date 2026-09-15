package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.*;
import com.guard.vaultguard.entities.enums.RiskLevel;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> isTransactionScored() {
        return (root, criteriaQuery, criteriaBuilder) -> {

            // join the transaction to riskManagement
            root.join(Transaction_.riskManagement);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Transaction> hasSenderBankCode(String senderBankCode) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (senderBankCode == null || senderBankCode.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering if senderBankId is null
            }

            // Join the Transaction entity with the Bank entity using the senderBank relationship
            Join<Transaction, Bank> joinedTrxWithBank = root.join(Transaction_.senderBank);
            return criteriaBuilder.equal(joinedTrxWithBank.get(Bank_.bankCode), senderBankCode);
        };
    }

    public static Specification<Transaction> hasTransactionStatus(TransactionStatus transactionStatus) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (transactionStatus == null)
                return criteriaBuilder.conjunction();

            Join<Transaction, RiskManagement> joinedTrxWithRiskManagement = root.join(Transaction_.riskManagement);
            return criteriaBuilder.equal(joinedTrxWithRiskManagement.get(RiskManagement_.transactionStatus), transactionStatus);
        };
    }

    public static Specification<Transaction> hasTransactionRiskLevel(RiskLevel transactionRiskLevel) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (transactionRiskLevel == null)
                return criteriaBuilder.conjunction();

            Join<Transaction, RiskManagement> joinedTrxWithRiskManagement = root.join(Transaction_.riskManagement);
            return criteriaBuilder.equal(joinedTrxWithRiskManagement.get(RiskManagement_.riskLevel), transactionRiskLevel);
        };
    }

    public static Specification<Transaction> hasTransactionType(TransactionType transactionType) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (transactionType == null)
                return criteriaBuilder.conjunction();

            return criteriaBuilder.equal(root.get(Transaction_.transactionType), transactionType);
        };
    }

    // starting from dateFrom
    public static Specification<Transaction> hasTransactionDateFrom(LocalDateTime dateFrom) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (dateFrom == null)
                return criteriaBuilder.conjunction();

            return criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.transactionDate), dateFrom);
        };
    }

    // ending at dateTo
    public static Specification<Transaction> hasTransactionDateTo(LocalDateTime dateTo) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (dateTo == null)
                return criteriaBuilder.conjunction();

            return criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.transactionDate), dateTo);
        };
    }
}
