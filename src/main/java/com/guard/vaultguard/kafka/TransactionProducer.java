package com.guard.vaultguard.kafka;

import com.guard.vaultguard.entities.Transaction;
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

    public void sendTransaction(Transaction trx){
        log.info("Sending transaction to Kafka: {}", trx.toString());
        kafkaTemplate.send(TransactionTopic, trx);
    }
}
