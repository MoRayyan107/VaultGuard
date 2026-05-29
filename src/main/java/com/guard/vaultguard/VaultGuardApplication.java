package com.guard.vaultguard;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class VaultGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultGuardApplication.class, args);
    }

    public NewTopic createTopicTransaction_In(){
        return TopicBuilder.name("${app.kafka.topic}")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
