package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.transaction.TransactionRequest;

import com.guard.vaultguard.entities.RiskManagement;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.RiskLevel;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.exceptions.*;
import com.guard.vaultguard.kafka.TransactionProducer;
import com.guard.vaultguard.repositories.RiskManagmentRepository;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.repositories.TransactionSpecification;
import com.guard.vaultguard.service.util.TransactionUtil;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.guard.vaultguard.config.Constants.RISKSCORE_THRESHOLD;
import static com.guard.vaultguard.config.Constants.MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS;


@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StringRedisTemplate redisTemplate;
    private final TransactionProducer transactionProducer;
    private final BankService bankService;
    private final TransactionUtil transactionUtil;
    private final RiskManagmentService riskManagmentService;
    private final RiskManagmentRepository riskManagmentRepository;

    public Transaction processTransaction(TransactionRequest trx){
        if (!validateTransaction(trx)) throw new IllegalTransactionException("Invalid transaction data");
        if (!checkDuplicateTransaction(trx)) throw new DuplicateTransactionException("Duplicate transaction detected");

        try {
            Transaction returnedTransaction = transactionUtil.saveTransaction(trx);
            transactionProducer.sendTransaction(returnedTransaction);
            log.info("[INFO] Transaction saved with ID: {}, and sent to Kafka", returnedTransaction.getId());
            return returnedTransaction;

        } catch (DataIntegrityViolationException ex){
            log.warn("[WARN] Data integrity violation while saving transaction: {}", ex.getMessage());
            return transactionUtil.getTransactionByReference(trx.getBankTrxReference());
        }
    }

    public Page<Transaction> getAllTransactions(String bankCode, String status,
                                                String riskLevel, String transactionType,
                                                String dateFrom, String dateTo,
                                                Pageable pageable) {
        // sanitise the input parameters to ensure they are in a consistent format for comparison
        String normalisedBankCode = bankCode != null ? bankCode.trim().toUpperCase() : null;
        String normalisedStatus = status != null ? status.trim().toUpperCase() : null;
        String normalisedRiskLevel = riskLevel != null ? riskLevel.trim().toUpperCase() : null;
        String normalisedTransactionType = transactionType != null ? transactionType.trim().toUpperCase() : null;
        String normalisedDateFrom = dateFrom != null ? dateFrom.trim() : null;
        String normalisedDateTo = dateTo != null ? dateTo.trim() : null;

        // parse the enums and throw exceptions if invalid values are provided
        RiskLevel trxRiskLevel = parseEnum(RiskLevel.class, normalisedRiskLevel,
                () -> new IllegalRiskLevelException("Invalid risk level: " + normalisedRiskLevel));
        TransactionStatus trxStatus = parseEnum(TransactionStatus.class, normalisedStatus,
                () -> new IllegalTransactionStatusException("Invalid transaction status: " + normalisedStatus));
        TransactionType trxType = parseEnum(TransactionType.class, normalisedTransactionType,
                () -> new IllegalTransactionTypeException("Invalid transaction type: " + normalisedTransactionType));

        // Format the date from and to
        LocalDateTime dateFromParsed = parseDateTime(normalisedDateFrom, LocalDate::atStartOfDay);
        LocalDateTime dateToParsed = parseDateTime(normalisedDateTo, localDate ->  localDate.atTime(LocalTime.MAX));

        // check the date range is valid, if both dates are provided
        if (dateFromParsed != null && dateToParsed != null && dateFromParsed.isAfter(dateToParsed)) {
            throw new IllegalArgumentException("Invalid date range: 'dateFrom' cannot be after 'dateTo'");
        } else if (dateFromParsed != null && dateToParsed != null && dateToParsed.isBefore(dateFromParsed)) {
            throw new IllegalArgumentException("Invalid date range: 'dateTo' cannot be before 'dateFrom'");
        }

        // create default sorting
        if (pageable.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Direction.DESC, "transactionDate") // sort based on transactionDate
                    .and(Sort.by(Sort.Direction.DESC, "id"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        // Do the Specifications for dynamic queries
        Specification<Transaction> specs = Specification.unrestricted();
        // add unconditional spec to only return transactions that have been scored (i.e. have a risk score)
        specs = specs.and(TransactionSpecification.isTransactionScored());

        if (normalisedBankCode != null)
            specs = specs.and(TransactionSpecification.hasSenderBankCode(normalisedBankCode));
        if (trxStatus != null)
            specs = specs.and(TransactionSpecification.hasTransactionStatus(trxStatus));
        if (trxRiskLevel != null)
            specs = specs.and(TransactionSpecification.hasTransactionRiskLevel(trxRiskLevel));
        if (trxType != null)
            specs = specs.and(TransactionSpecification.hasTransactionType(trxType));
        if (dateFromParsed != null)
            specs = specs.and(TransactionSpecification.hasTransactionDateFrom(dateFromParsed));
        if (dateToParsed != null)
            specs = specs.and(TransactionSpecification.hasTransactionDateTo(dateToParsed));

        return transactionRepository.findAll(specs, pageable);
    }

    public Transaction getTransactionById(UUID tsxId){
        return transactionRepository.findById(tsxId)
                .orElseThrow(() -> new IllegalTransactionException("Transaction with id " + tsxId + " not found"));
    }

    @Transactional
    public void calculateRiskScore(Transaction trx){
        double riskScore = 0.0;
        String accountKey = trx.getSenderAccountNumber();
        String rateKey = redisKey(accountKey, "rate");
        String locationKey = redisKey(accountKey, "lastKnownLocation");
        String timestampKey = redisKey(accountKey, "timestamp");

        // check if the amount is greater than 50K
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
                if ( timeDiff <= MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS) {riskScore += 0.3;}
            }
        }

        redisTemplate.opsForValue().set(locationKey, trx.getSenderLocation());
        redisTemplate.opsForValue().set(timestampKey, String.valueOf(getCurrentTimeStamp_Millis()));
        updateRiskScore(trx, riskScore);
    }

    @Transactional
    public void updateRiskScore(Transaction trx, double score){
        if (riskManagmentRepository.existsByTransaction_Id(trx.getId())) {
            log.info("[INFO] Risk score already exists for Transaction with ID: {}", trx.getId());
            return;
        }

        try {
            // round the ccore to near value 0.600001 -> 0.6
            score = Math.round(score * 10.0) / 10.0;

            TransactionStatus status = score >= RISKSCORE_THRESHOLD ? TransactionStatus.FLAGGED : TransactionStatus.COMPLETED;

            RiskManagement rm = RiskManagement.builder()
                    .transaction(trx)
                    .riskScore(score)
                    .riskLevel(riskManagmentService.getLevel(score))
                    .transactionStatus(status)
                    .reason(score >= RISKSCORE_THRESHOLD ? "High risk transaction" : "Normal transaction")
                    .createdAt(LocalDateTime.now()).build();

            RiskManagement savedRisk = riskManagmentRepository.save(rm);

            log.info("[INFO] SUCCESS RISK SAVE");
            log.info("[INFO] Risk Score for Transaction Id: {} -> {}, Level: {}", trx.getId(), savedRisk.getRiskScore(), savedRisk.getRiskLevel());
        }
        catch (DataIntegrityViolationException e) {
            log.info("[INFO] FOUND DUPLICATE RISK MANAGEMENT FOR TRANSACTION ID: {}", trx.getId(), e);
        }
    }

    private boolean checkDuplicateTransaction(TransactionRequest trxReq) {
        // old way make a key with sender and receiver acc number and amount,
        // but this doesnt solve multi-bank transactions and can break,
        // also to pretect from duploicates we can use the bankTrxReference as a unique key for each transaction

        // since each transaction from bank has a unique reference, we can use that as a key to check for duplicates
        // key -> "idempotency:{senderBankCode}:{bankTrxReference}"
        String redisKey = "idempotency:" + trxReq.getSenderBankCode() + ":" + trxReq.getBankTrxReference();
        String redisValue = UUID.randomUUID().toString();

        Boolean isUnique = redisTemplate.opsForValue().setIfAbsent(redisKey, redisValue, 2, TimeUnit.MINUTES);
        System.out.println("Redis key: " + redisTemplate.keys(redisKey).stream().findFirst().orElse("Not found") + ", Redis value: " + redisValue + ", isUnique: " + isUnique + "TTL: " + redisTemplate.getExpire(redisKey) + " seconds");

        // returns TRUE if created, FALSE if exists
        return isUnique != null && isUnique;
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

        if (trx.getBankTrxReference() == null || trx.getBankTrxReference().isEmpty()) return false;

        if (trx.getAmount() == null || trx.getAmount().doubleValue() <= 0) return false;

        return trx.getSenderLocation() != null && !trx.getSenderLocation().isEmpty();
    }

    // This method is a generic utility to parse a string into an enum value of the specified type. If the parsing fails, it throws a custom exception provided by the exceptionSupplier.
    // can keep is static for utility class, but here we keep it private for this service class
    private <T extends Enum<T>> T parseEnum(Class<T> enumType, String value, Supplier<RuntimeException> exceptionSupplier ) {
        if (value == null ) return null;
        if (value.isEmpty()) throw exceptionSupplier.get();

        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw exceptionSupplier.get();
        }
    }

    private LocalDateTime parseDateTime(String date, Function<LocalDate, LocalDateTime> function) {
        if (date == null) return null;
        if (date.isEmpty()) throw new IllegalArgumentException("Date cannot be empty: " + date);

        try {
            LocalDate localDate = LocalDate.parse(date);
            return function.apply(localDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

}
