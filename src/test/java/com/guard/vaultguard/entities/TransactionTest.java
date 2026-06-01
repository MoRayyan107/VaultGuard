package com.guard.vaultguard.entities;

import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private Transaction transaction;

    private static final String SENDER_ACCOUNT_NUMBER = "1234567890";
    private static final String RECIPIENT_ACCOUNT_NUMBER = "0987654321";
    private static final String SENDER_LOCATION = "New York";

    private static final Long TRANSACTION_ID = 10101L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.00);

    private static final TransactionType DEFAULT_TRANSFER = TransactionType.TRANSFER;
    private static final TransactionType SET_TYPE_DEPOSIT = TransactionType.DEPOSIT;

    private static final TransactionStatus DEFAULT_STATUS_PENDING = TransactionStatus.PENDING;
    private static final TransactionStatus SET_STATUS_COMPLETED = TransactionStatus.COMPLETED;

    private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2024, 6, 1, 12 ,0);

    @BeforeEach
    public void setUp() {
        transaction = Transaction.builder()
                .id(TRANSACTION_ID)
                .senderAccountNumber(SENDER_ACCOUNT_NUMBER)
                .recipientAccountNumber(RECIPIENT_ACCOUNT_NUMBER)
                .amount(AMOUNT)
                .senderLocation(SENDER_LOCATION)
                .transactionType(DEFAULT_TRANSFER)
                .transactionStatus(DEFAULT_STATUS_PENDING) // Set to null for testing purposes
                .transactionDate(DEFAULT_DATE) // Set to null for testing purposes
                .build();
    }

    @AfterEach
    public void tearDown() {
        transaction = null;
    }

    @Test
    public void Getters() {
        assertEquals(TRANSACTION_ID, transaction.getId());
        assertEquals(SENDER_ACCOUNT_NUMBER, transaction.getSenderAccountNumber());
        assertEquals(RECIPIENT_ACCOUNT_NUMBER, transaction.getRecipientAccountNumber());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals(SENDER_LOCATION, transaction.getSenderLocation());
        assertEquals(DEFAULT_TRANSFER, transaction.getTransactionType());
        assertEquals(DEFAULT_STATUS_PENDING, transaction.getTransactionStatus());
        assertEquals(DEFAULT_DATE, transaction.getTransactionDate());
    }

    @Test
    public void Setters() {
        transaction.setId(12345L);
        assertEquals(12345L, transaction.getId());

        transaction.setSenderAccountNumber("10101010101");
        assertEquals("10101010101", transaction.getSenderAccountNumber());

        transaction.setRecipientAccountNumber("0987654321");
        assertEquals("0987654321", transaction.getRecipientAccountNumber());

        transaction.setAmount(BigDecimal.valueOf(110.00));
        assertEquals(BigDecimal.valueOf(110.00), transaction.getAmount());

        transaction.setSenderLocation("New Orleans");
        assertEquals("New Orleans", transaction.getSenderLocation());

        transaction.setTransactionType(SET_TYPE_DEPOSIT);
        assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());

        transaction.setTransactionStatus(SET_STATUS_COMPLETED);
        assertEquals(TransactionStatus.COMPLETED, transaction.getTransactionStatus());

        LocalDateTime newDate = LocalDateTime.of(2024, 6, 2, 15, 30);
        transaction.setTransactionDate(newDate);
        assertEquals(newDate, transaction.getTransactionDate());
    }
}
