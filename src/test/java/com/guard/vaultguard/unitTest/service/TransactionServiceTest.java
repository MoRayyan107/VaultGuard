package com.guard.vaultguard.unitTest.service;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.kafka.TransactionProducer;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.guard.vaultguard.Constants.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequest transactionRequest;
    private Transaction baseTransaction;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest(
                SENDER_ACCOUNT_NUMBER,
                RECEIVER_ACCOUNT_NUMBER,
                AMOUNT_GREATER_50_THOUSAND,
                SENDER_LOCATION,
                TransactionType.TRANSFER
        );

        transactionId = UUID.randomUUID();
        baseTransaction = Transaction.builder()
                .id(transactionId)
                .senderAccountNumber(SENDER_ACCOUNT_NUMBER)
                .recipientAccountNumber(RECEIVER_ACCOUNT_NUMBER)
                .amount(AMOUNT_GREATER_50_THOUSAND)
                .senderLocation(SENDER_LOCATION)
                .transactionType(TransactionType.TRANSFER)
                .transactionStatus(TransactionStatus.PENDING)
                .build();
    }

    // first the defaut service things
    @Test
    void getFlaggedTransactions_shouldReturnListOfTransaction(){
        UUID transactionID = UUID.randomUUID();
        Transaction trx = new Transaction(
                transactionID,
                SENDER_ACCOUNT_NUMBER,
                SENDER_LOCATION,
                AMOUNT_GREATER_50_THOUSAND,
                RECEIVER_ACCOUNT_NUMBER,
                TransactionType.TRANSFER,
                TransactionStatus.FLAGGED,
                LocalDateTime.of(2024, 6, 1, 12, 0),
                RISK_SCORE_0_7,
                LocalDateTime.of(2024, 6, 1, 12,1)
        );

        when(transactionRepository.findByTransactionStatus(TransactionStatus.FLAGGED))
                .thenReturn(List.of(trx));

        List<Transaction> flaggedTrx = transactionService.getFlaggedTransactions();
        Transaction firstTransaction = flaggedTrx.getFirst();

        assertThat(flaggedTrx).hasSize(1).contains(trx);
        assertThat(firstTransaction.getId()).isEqualTo(transactionID);
        assertThat(firstTransaction.getRiskScore()).isEqualTo(RISK_SCORE_0_7);
        assertThat(firstTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.FLAGGED);
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransaction() {
        UUID transactionID = UUID.randomUUID();
        Transaction flaggedTrx = new Transaction(
                transactionID,
                SENDER_ACCOUNT_NUMBER,
                SENDER_LOCATION,
                AMOUNT_GREATER_50_THOUSAND,
                RECEIVER_ACCOUNT_NUMBER,
                TransactionType.TRANSFER,
                TransactionStatus.FLAGGED,
                LocalDateTime.of(2024, 6, 1, 12, 0),
                RISK_SCORE_0_7,
                LocalDateTime.of(2024, 6, 1, 12,1)
        );

        when(transactionService.getAllTransactions()).thenReturn(List.of(baseTransaction, flaggedTrx));

        List<Transaction> allFetchTransactions = transactionService.getAllTransactions();

        assertThat(allFetchTransactions).hasSize(2).contains(baseTransaction, flaggedTrx);
        assertThat(allFetchTransactions.getFirst().getId()).isEqualTo(baseTransaction.getId());
        assertThat(allFetchTransactions.getFirst().getTransactionStatus()).isEqualTo(TransactionStatus.PENDING);

        assertThat(allFetchTransactions.getLast().getId()).isEqualTo(flaggedTrx.getId());
        assertThat(allFetchTransactions.getLast().getTransactionStatus()).isEqualTo(TransactionStatus.FLAGGED);
    }

    @Test
    void processTransaction_savesAndPublishesTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction trx = invocation.getArgument(0);
            trx.setId(transactionId);
            return trx;
        });
        doNothing().when(transactionProducer).sendTransaction(any(Transaction.class));

        Transaction result = transactionService.processTransaction(transactionRequest);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        verify(transactionProducer).sendTransaction(result);

        assertThat(result.getId()).isEqualTo(transactionId);
        assertThat(captor.getValue().getSenderAccountNumber()).isEqualTo(SENDER_ACCOUNT_NUMBER);
        assertThat(captor.getValue().getRecipientAccountNumber()).isEqualTo(RECEIVER_ACCOUNT_NUMBER);
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo(AMOUNT_GREATER_50_THOUSAND);
        assertThat(captor.getValue().getTransactionType()).isEqualTo(TransactionType.TRANSFER);
        // its pending at start
        assertThat(captor.getValue().getTransactionStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(captor.getValue().getTransactionDate()).isNotNull();
    }

    @Test
    void processTransaction_invalidRequestThrowsException() {
        transactionRequest.setSenderLocation("");

        assertThatThrownBy(() -> transactionService.processTransaction(transactionRequest))
                .isInstanceOf(IllegalTransactionException.class)
                .hasMessage("Invalid transaction data");
    }

    @Test
    void getTransactionById_returnsStoredTransaction() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(baseTransaction));

        Transaction result = transactionService.getTransactionById(transactionId);

        assertThat(result).isSameAs(baseTransaction);
    }

    @Test
    void calculateRiskScore_updatesTransactionInPlace() {
        baseTransaction.setAmount(AMOUNT_GREATER_100_THOUSAND);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(baseTransaction));

        double score = transactionService.calculateRiskScore(baseTransaction);

        assertThat(score).isEqualTo(0.3d);
        assertThat(baseTransaction.getRiskScore()).isEqualTo(0.3d);
        assertThat(baseTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(baseTransaction.getResolvedAt()).isNotNull();
    }

    @Test
    void calculateRiskScore_multipleTransactionsIncreasesRiskScore() {
        // baseTransaction already has AMOUNT_GREATER_50_THOUSAND and TransactionType.TRANSFER

        // making this transaction for the 6th time under 60sec
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(6L); // addes 0.2 score
        when(valueOperations.get(anyString())).thenReturn(null);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(baseTransaction));

        // verify the values
        double score = transactionService.calculateRiskScore(baseTransaction);

        assertThat(score).isEqualTo(0.4d); // 0.1 for amount + 0.1 for type + 0.2 for frequency
        assertThat(baseTransaction.getRiskScore()).isEqualTo(0.4d);
        assertThat(baseTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(baseTransaction.getResolvedAt()).isNotNull();
    }
}
