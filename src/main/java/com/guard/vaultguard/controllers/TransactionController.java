package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.dto.transaction.TransactionResponse;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.guard.vaultguard.config.Constants.ROLE_ANALYST;
import static com.guard.vaultguard.config.Constants.ROLE_MANAGER;


@RestController
@RequestMapping("api/v1/fraudDetect")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/processTransaction")
    public ResponseEntity<TransactionResponse> processTransaction(
            @Valid @RequestBody TransactionRequest trxReq
    )
    {
        Transaction trx = transactionService.processTransaction(trxReq);
        TransactionResponse trxResponse = TransactionResponse.buildTransactionResponse(trx);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/flaggedTransactions")
    public ResponseEntity<List<TransactionResponse>> getFlaggedTransactions(){
        List<Transaction> trxs = transactionService.getFlaggedTransactions();

        List<TransactionResponse> trxResponse = TransactionResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/highRiskTransactions")
    public ResponseEntity<List<TransactionResponse>> getAllHighRiskTransactions(){
        List<Transaction> trxs = transactionService.getAllHighRiskTransactions();

        List<TransactionResponse> trxResponse = TransactionResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/allTransactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        List<Transaction> trxs = transactionService.getAllTransactions();

        List<TransactionResponse> trxResponse = TransactionResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"')")
    @GetMapping("/fetch/transactionById/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id){
        TransactionResponse trxRes = TransactionResponse.buildTransactionResponse(transactionService.getTransactionById(id));

        return ResponseEntity.ok(trxRes);
    }


}
