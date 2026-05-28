package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.TransactionDTO;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction processTransaction(TransactionDTO trx){
        if (!validTransaction(trx)) throw new IllegalTransactionException("Invalid transaction data");

        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(trx.getSenderAccountNumber());
        transaction.setRecipientAccountNumber(trx.getRecipientAccountNumber());
        transaction.setAmount(trx.getAmount());
        transaction.setTransactionType(trx.getTransactionType());
        transaction.setSenderLocation(trx.getSenderLocation());

        // set the default values
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getFlaggedTransactions(){
        return transactionRepository.findByTransactionStatus(TransactionStatus.FLAGGED);
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long tsxId){
        return transactionRepository.findById(tsxId)
                .orElseThrow(() -> new IllegalTransactionException("Transaction with id " + tsxId + " not found"));
    }

    public List<Transaction> getAllHighRiskTransactions(double riskScoreThreshold){
        return transactionRepository.findByRiskScoreGreaterThan(riskScoreThreshold);
    }

    // ONLY CALLS WHEN COMPLETED TRANSACTION
    public void updateRiskScore(Long tsxId, double score){
        Transaction tsx = getTransactionById(tsxId);
        tsx.setRiskScore(score);
        if (score >= 0.7) tsx.setTransactionStatus(TransactionStatus.FLAGGED);
        else tsx.setTransactionStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(tsx);
    }

    private boolean validTransaction(TransactionDTO trx){
        if (trx.getSenderAccountNumber() == null || trx.getSenderAccountNumber().isEmpty()) return false;

        if (trx.getTransactionType().toString().isEmpty()) return false;

        if (trx.getTransactionType() == TransactionType.TRANSFER) {
            if (trx.getRecipientAccountNumber() == null || trx.getRecipientAccountNumber().isEmpty()) return false;
        }

        if (trx.getAmount() == null || trx.getAmount().doubleValue() <= 0) return false;

        if (trx.getSenderLocation() == null || trx.getSenderLocation().isEmpty()) return false;

        return true;
    }
}
