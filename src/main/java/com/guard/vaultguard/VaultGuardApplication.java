package com.guard.vaultguard;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;


// TODO: add @Value for ${app.kafka.topic} and use it in createTopicTransaction_In() method
// TODO: update the tes case to actually pass this condition (ad @TestPropertySource)

@SpringBootApplication
public class VaultGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultGuardApplication.class, args);
    }

    @Value("${app.kafka.topic}")
    private String TransactionTopic;

    @Bean
    public NewTopic createTopicTransaction_In(){
        return TopicBuilder.name(TransactionTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
