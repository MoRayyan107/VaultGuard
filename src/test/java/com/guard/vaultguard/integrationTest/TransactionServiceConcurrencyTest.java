package com.guard.vaultguard.integrationTest;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.service.TransactionService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.guard.vaultguard.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
// TODO: Redis consurenccy is done, Kafka is left
class TransactionServiceConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final int THREAD_COUNT = 1_000;
    private static final long EXPECTED_PENDING_TRANSACTIONS = 0;

    // setup for redis ccontainer
    @Container
    @ServiceConnection // gets redis host and port form spring data redis properties
    static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7.2.0"));

    @Container
    @ServiceConnection
    static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.6")
            .withDatabaseName("vaultguard_test");

    private static final TransactionRequest TRANSACTION_REQUEST_TEST = new TransactionRequest(
            SENDER_ACCOUNT_NUMBER,
            RECEIVER_ACCOUNT_NUMBER,
            AMOUNT_GREATER_100_THOUSAND,
            SENDER_LOCATION,
            TransactionType.TRANSFER
    );

    @Test
    void isContainerRunning(){
        assertThat(kafkaContainer.isCreated()).isTrue();
        assertThat(kafkaContainer.isRunning()).isTrue();
        System.out.println("Kafka container is running on: " + kafkaContainer.getBootstrapServers());

        assertThat(redisContainer.isCreated()).isTrue();
        assertThat(redisContainer.isRunning()).isTrue();
        System.out.println("Redis container is running on: " + redisContainer.getHost() + ":" + redisContainer.getFirstMappedPort());

        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
        System.out.println("PostgreSQL container is running on: "
                + postgresContainer.getJdbcUrl()
                + " with username: " + postgresContainer.getUsername());
    }

    @Test
    void makeTransaction() throws InterruptedException {
        boolean isCI = "true".equalsIgnoreCase(System.getenv("CI"));
        int threadCount = isCI ? 50 : THREAD_COUNT;

        if (!isCI)
            System.out.println("Running in local environment. Using thread count: " + threadCount);
        else
            System.out.println("Running in CI environment. Using thread count: " + threadCount);

        // Cyclic Barrier for synchronizing threads
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        List<Future<?>> futuresList = new ArrayList<>();

        // Execcutor Service to manage threads
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < threadCount; i++) {
                Future<?> future = executorService.submit(() -> {
                    try {
                        barrier.await(); // Wait for all threads to be ready
                        transactionService.processTransaction(TRANSACTION_REQUEST_TEST);
                    } catch (Exception e) {
                        System.out.println("Error in thread execution: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
                futuresList.add(future);
            }
        } catch (Exception e) {
            System.out.println("Error in executor service: " + e.getMessage());
        }

        // loop into future Object to see any Exxcceptions are thrown
        for (Future<?> future : futuresList) {
            assertDoesNotThrow(() -> future.get(), "Exception occurred during transaction processing");
        }

        // wait for kafka to process all messages
        await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted( () -> {
                    // chek if rate is same as the threads
                    String rateKey = "transaction:"+SENDER_ACCOUNT_NUMBER+":rate:";
                    String userRate = redisTemplate.opsForValue().get(rateKey);
                    assertNotNull(userRate);
                    System.out.println("User rate from Redis: " + userRate);

                    assertThat(Long.parseLong(userRate)).isEqualTo((long) threadCount);
                    System.out.println("Expected rate: " + threadCount + ", Actual rate: " + userRate);

                    // get the cccont of transaction with pending
                    long pendingTransactionCount = transactionRepository.countGetTransactionsByTransactionStatus(TransactionStatus.PENDING);
                    assertThat(pendingTransactionCount).isEqualTo(EXPECTED_PENDING_TRANSACTIONS);
                    System.out.println("Expected pending transactions: " + EXPECTED_PENDING_TRANSACTIONS + ", Actual pending transactions: " + pendingTransactionCount);
                });
    }

}