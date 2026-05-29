package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.transaction.TransactionRequest;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.repositories.TransactionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    private static final double RISKSCORE_THRESHOLD = 0.7;

    private final TransactionRepository transactionRepository;
    private final RedisTemplate<Object, Object> redisTemplate;

    public TransactionService(TransactionRepository transactionRepository, RedisTemplate<Object, Object> redisTemplate) {
        this.transactionRepository = transactionRepository;
        this.redisTemplate = redisTemplate;
    }

    public Transaction processTransaction(TransactionRequest trx){
        if (!validTransaction(trx)) throw new IllegalTransactionException("Invalid transaction data");

        Transaction transaction = Transaction.builder()
                .senderAccountNumber(trx.getSenderAccountNumber())
                .recipientAccountNumber(trx.getRecipientAccountNumber())
                .amount(trx.getAmount())
                .transactionType(trx.getTransactionType())
                .senderLocation(trx.getSenderLocation())

                // default values when making a transaction
                .transactionStatus(TransactionStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();

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

    public List<Transaction> getAllHighRiskTransactions(){
        return transactionRepository.findByRiskScoreGreaterThan(RISKSCORE_THRESHOLD);
    }


    public void calculateRiskScore(Transaction trx){
        double riskScore = 0.0;

        // check if the ammount is graeter than 50K
        if (trx.getAmount().doubleValue() > 50_000) riskScore += 0.1;
        if (trx.getAmount().doubleValue() > 100_000) riskScore += 0.2;

        // type of transaction
        if (trx.getTransactionType() == TransactionType.TRANSFER) riskScore += 0.1;

        // frequency of transactions
        Integer freqTransaction = (Integer) redisTemplate.opsForValue().get("transaction_rate:"+trx.getSenderAccountNumber());

        if (freqTransaction == null) {
            freqTransaction = 1;
            redisTemplate.opsForValue().set("transaction_rate:"+trx.getSenderAccountNumber(), freqTransaction);
            redisTemplate.expire("transaction_rate:"+trx.getSenderAccountNumber(), 60,  TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment("transaction_rate:" + trx.getSenderAccountNumber());
        }
        if (freqTransaction > 5) riskScore += 0.2;


        // location keeps chaning in short period
        String lastKnownLocation = (String) redisTemplate.opsForValue().get("transaction_location:"+trx.getSenderAccountNumber());
        Long timeStampLocation = (Long) redisTemplate.opsForValue().get("location_timestamp:"+trx.getSenderAccountNumber());

        if (lastKnownLocation == null || timeStampLocation == null) {
            redisTemplate.opsForValue().set("transaction_location:"+trx.getSenderAccountNumber(), trx.getSenderLocation());

            redisTemplate.opsForValue().set("location_timestamp:"+trx.getSenderAccountNumber(), getCurrentTimeStamp_Millis());
        } else {
            if (!trx.getSenderLocation().equals(lastKnownLocation)){

                Long TimeDiff = (getCurrentTimeStamp_Millis() - timeStampLocation)/1000;
                if (TimeDiff >= 120 && TimeDiff <= 300) {
                    riskScore += 0.3;
                }
            }
            redisTemplate.opsForValue().set("transaction_location:"+trx.getSenderAccountNumber(), trx.getSenderLocation());
            redisTemplate.opsForValue().set("location_timestamp:"+trx.getSenderAccountNumber(), getCurrentTimeStamp_Millis());
        }
        updateRiskScore(trx.getId(), riskScore);
    }


    // ONLY CALLS WHEN COMPLETED TRANSACTION
    public void updateRiskScore(Long tsxId, double score){
        Transaction tsx = getTransactionById(tsxId);
        tsx.setRiskScore(score);
        if (score >= 0.7) tsx.setTransactionStatus(TransactionStatus.FLAGGED);
        else tsx.setTransactionStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(tsx);
    }

    private Long getCurrentTimeStamp_Millis(){
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    private boolean validTransaction(TransactionRequest trx){
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
