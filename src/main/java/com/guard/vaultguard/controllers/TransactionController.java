package com.guard.vaultguard.controllers;

import com.guard.vaultguard.service.TransactionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // create processTransaction endpoints
    // create some fetch  endpoints (getFlagged, getAllTransaction, getTransactionBy id
    // create update risk endpoint
}
