package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.entities.Bank_;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.Transaction_;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> hasSenderBankId(UUID senderBankId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (senderBankId == null) {
                return criteriaBuilder.conjunction(); // No filtering if senderBankId is null
            }

            // Join the Transaction entity with the Bank entity using the senderBank relationship
            Join<Transaction, Bank> joinedTrxWithBank = root.join(Transaction_.senderBank);
            return criteriaBuilder.equal(joinedTrxWithBank.get(Bank_.bankId), senderBankId);
        };
    }
}
