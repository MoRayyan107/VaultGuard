package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.dto.transaction.TransactionResponse;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class TransactionServiceTest {

    @MockitoBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    // DTOS
    private TransactionRequest transactionRequest;
    private TransactionResponse transactionResponse;

    private Transaction transaction;

    @BeforeEach
    public void setup() {
        transactionRequest = new TransactionRequest();
        transactionResponse = new TransactionResponse();
    }
}
