package com.guard.vaultguard.kafka;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(@NotNull Transaction trx){
        log.info("[INFO] Consuming transaction from Kafka: {}", trx.toString());
        double resultRiskScore = transactionService.calculateRiskScore(trx);
        log.info("[INFO] Risk score Calculated for Transaction {} : {}",trx.getId(), resultRiskScore);
    }

}
