package com.guard.vaultguard.ServiceTest;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.exceptions.BankNotActiveException;
import com.guard.vaultguard.exceptions.DuplicateTransactionException;
import com.guard.vaultguard.kafka.TransactionProducer;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.service.BankService;
import com.guard.vaultguard.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.guard.vaultguard.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private BankService bankService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TransactionRequest trxRequest;
    private Bank senderBank;
    private Bank receiverBank;

    @BeforeEach
    void setUp() {
        trxRequest = TransactionRequest.builder()
                .senderAccountNumber(SENDER_ACCOUNT_NUMBER)
                .senderBankCode(SENDER_BANK_CODE)
                .bankTrxReference(BANK_REFERENCE)
                .recipientAccountNumber(RECIPIENT_ACCOUNT_NUMBER)
                .recipientBankCode(RECIPIENT_BANK_CODE)
                .amount(NORMAL_TRANSACTION_AMOUNT)
                .senderLocation(SENDER_LOCATION)
                .transactionType(TRANSFER_TRANSACTION_TYPE)
                .build();

        senderBank = Bank.builder()
                .bankId(UUID.randomUUID())
                .bankCode(SENDER_BANK_CODE)
                .bankName("Sender Bank")
                .active(true)
                .build();

        receiverBank = Bank.builder()
                .bankId(UUID.randomUUID())
                .bankCode(RECIPIENT_BANK_CODE)
                .bankName("Receiver Bank")
                .active(true)
                .build();
    }

    @Nested
    class ProcessTransactionTests {

        @Test
        void processTransaction_shouldProcessTransactionSuccessfully() {
            // Arrange
            Transaction savedTransaction = Transaction.builder()
                    .id(UUID.randomUUID())
                    .senderAccountNumber(SENDER_ACCOUNT_NUMBER)
                    .senderBank(senderBank)
                    .senderLocation(SENDER_LOCATION)
                    .transactionReference(BANK_REFERENCE)
                    .amount(NORMAL_TRANSACTION_AMOUNT)
                    .recipientAccountNumber(RECIPIENT_ACCOUNT_NUMBER)
                    .recipientBank(receiverBank)
                    .transactionType(TRANSFER_TRANSACTION_TYPE)
                    .build();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), eq(2L), eq(TimeUnit.MINUTES))).thenReturn(Boolean.TRUE);
            when(bankService.getBankByCode(SENDER_BANK_CODE)).thenReturn(senderBank);
            when(bankService.getBankByCode(RECIPIENT_BANK_CODE)).thenReturn(receiverBank);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // Act
            Transaction result = transactionService.processTransaction(trxRequest);

            // Assert
            assertNotNull(result);
            assertEquals(savedTransaction.getId(), result.getId());
        }

        @Test
        void processTransaction_shouldThrowExceptionWhenSenderBankIsInactive() {
            // Arrange
            senderBank.setActive(false);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), eq(2L), eq(TimeUnit.MINUTES))).thenReturn(Boolean.TRUE);
            when(bankService.getBankByCode(SENDER_BANK_CODE)).thenThrow(new BankNotActiveException("Sender bank is not active"));

            // Act & Assert
           assertThrows(BankNotActiveException.class, () -> transactionService.processTransaction(trxRequest));
        }

        @Test
        void processTransaction_shouldThrowExceptionWhenTransactionIsDuplicated() {
            // Arrange
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), eq(2L), eq(TimeUnit.MINUTES))).thenReturn(Boolean.FALSE);

            // Act and assert
            assertThrows(DuplicateTransactionException.class, () -> transactionService.processTransaction(trxRequest));
        }
    }
}
