package com.guard.vaultguard.kafka;

import com.guard.vaultguard.entities.Transaction;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String TransactionTopic;

    public TransactionProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(@NotNull Transaction trx){
        kafkaTemplate.send(TransactionTopic, trx)
                .whenComplete((res, exception) -> {
                    if (exception != null){
                        log.error("[ERROR] Sending transaction to Kafka: {}", trx.toString(), exception);
                    } else {
                        log.info("[INFO] Sending transaction to Kafka: {}", trx.toString());
                    }
                });
    }
}
