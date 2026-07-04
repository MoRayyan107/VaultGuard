package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.transaction.TransactionRequest;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.kafka.TransactionProducer;
import com.guard.vaultguard.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.guard.vaultguard.config.Constants.RISKSCORE_THRESHOLD;
import static com.guard.vaultguard.config.Constants.MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS;
import static com.guard.vaultguard.config.Constants.MIN_TIME_DIFF_LOCATION_CHANGE_SECONDS;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StringRedisTemplate redisTemplate;
    private final TransactionProducer transactionProducer;

    private final static Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository,
                              StringRedisTemplate redisTemplate,
                              TransactionProducer transactionProducer)
    {
        this.transactionRepository = transactionRepository;
        this.redisTemplate = redisTemplate;
        this.transactionProducer = transactionProducer;
    }

    @Transactional
    public Transaction processTransaction(TransactionRequest trx){
        if (!validateTransaction(trx)) throw new IllegalTransactionException("Invalid transaction data");

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

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("[INFO] Transaction saved with ID: {}", savedTransaction.getId());
        transactionProducer.sendTransaction(savedTransaction);

        return savedTransaction;
    }

    public List<Transaction> getFlaggedTransactions(){
        return transactionRepository.findByTransactionStatus(TransactionStatus.FLAGGED);
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(UUID tsxId){
        return transactionRepository.findById(tsxId)
                .orElseThrow(() -> new IllegalTransactionException("Transaction with id " + tsxId + " not found"));
    }

    public List<Transaction> getAllHighRiskTransactions(){
        return transactionRepository.findByRiskScoreGreaterThan(RISKSCORE_THRESHOLD);
    }

    @Transactional
    public double calculateRiskScore(Transaction trx){
        double riskScore = 0.0;
        String accountKey = trx.getSenderAccountNumber();
        String rateKey = redisKey(accountKey, "rate");
        String locationKey = redisKey(accountKey, "lastKnownLocation");
        String timestampKey = redisKey(accountKey, "timestamp");

        // check if the amount is graeter than 50K
        if (trx.getAmount().doubleValue() >= 100_000) riskScore += 0.2;
        else if (trx.getAmount().doubleValue() >= 50_000) riskScore += 0.1;

        // type of transaction
        if (trx.getTransactionType() == TransactionType.TRANSFER) riskScore += 0.1;

        // frequency of transactions
        Long freqTransaction = redisTemplate.opsForValue().increment(rateKey);
        if (freqTransaction != null && freqTransaction == 1L) {
            redisTemplate.expire(rateKey, 60, TimeUnit.SECONDS);
        }
        if (freqTransaction != null && freqTransaction > 5) riskScore += 0.2;

        // location keeps chaning in short period
        String lastKnownLocation = redisTemplate.opsForValue().get(locationKey);
        String timeStampRaw = redisTemplate.opsForValue().get(timestampKey);
        Long timeStampLocation = timeStampRaw == null ? null : Long.parseLong(timeStampRaw);

        if (lastKnownLocation != null && timeStampLocation != null) {
            if (!trx.getSenderLocation().equals(lastKnownLocation)){

                // if the location changes within 2-5 mins (Country based in this version later Ill see on Lat and Long)
                long timeDiff = (getCurrentTimeStamp_Millis() - timeStampLocation) / 1000;
                if (
                        timeDiff >= MIN_TIME_DIFF_LOCATION_CHANGE_SECONDS &&
                                timeDiff <= MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS
                ) {
                    riskScore += 0.3;
                }
            }
        }

        redisTemplate.opsForValue().set(locationKey, trx.getSenderLocation());
        redisTemplate.opsForValue().set(timestampKey, String.valueOf(getCurrentTimeStamp_Millis()));
        return updateRiskScore(trx.getId(), riskScore);
    }


    // ONLY CALLS WHEN COMPLETED TRANSACTION AUTO SAVES IN DB
    @Transactional
    public double updateRiskScore(UUID tsxId, double score){
        // round the ccore to near value 0.600001 -> 0.6
        score = Math.round(score * 10.0) / 10.0;

        Transaction tsx = getTransactionById(tsxId);
        tsx.setRiskScore(score);

        if (score >= RISKSCORE_THRESHOLD) tsx.setTransactionStatus(TransactionStatus.FLAGGED);
        else tsx.setTransactionStatus(TransactionStatus.COMPLETED);

        // set the transaction as resolved 
        tsx.setResolvedAt(LocalDateTime.now());

        log.info("[INFO] Saved risk score for Transaction with ID: {}", tsxId);

        return tsx.getRiskScore();
    }

    private Long getCurrentTimeStamp_Millis(){
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String redisKey(String accountNumber, String suffix) {
        return "transaction:" + accountNumber + ":" + suffix + ":";
    }


    private boolean validateTransaction(TransactionRequest trx){
        if (trx.getSenderAccountNumber() == null || trx.getSenderAccountNumber().isEmpty()) return false;

        if (trx.getTransactionType() == null) return false;
        if (trx.getTransactionType().toString().isEmpty()) return false;

        if (trx.getTransactionType() == TransactionType.TRANSFER) {
            if (trx.getRecipientAccountNumber() == null || trx.getRecipientAccountNumber().isEmpty()) return false;
        }

        if (trx.getAmount() == null || trx.getAmount().doubleValue() <= 0) return false;

        return trx.getSenderLocation() != null && !trx.getSenderLocation().isEmpty();
    }

}
