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

    // TODO: create processTransaction endpoints
    // TODO: create some fetch  endpoints (getFlagged, getAllTransaction, getTransactionBy id
    // TODO: create update risk endpoint
}
