package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.dto.transaction.TransactionResponse;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.guard.vaultguard.config.Constants.ROLE_ANALYST;
import static com.guard.vaultguard.config.Constants.ROLE_MANAGER;


@RestController
@RequestMapping("api/v1/transaction")
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
        TransactionResponse trxResponse = buildTransactionResponse(trx);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/flaggedTransactions")
    public ResponseEntity<List<TransactionResponse>> getFlaggedTransactions(){
        List<TransactionResponse> trxResponse = new ArrayList<>();
        List<Transaction> tres = transactionService.getFlaggedTransactions();

        for (Transaction trx : tres){
            TransactionResponse trxRes = buildTransactionResponse(trx);

            trxResponse.add(trxRes);
        }

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/highRiskTransactions")
    public ResponseEntity<List<TransactionResponse>> getAllHighRiskTransactions(){
        List<TransactionResponse> trxResponse = new ArrayList<>();
        List<Transaction> trxs = transactionService.getAllHighRiskTransactions();

        for  (Transaction trx : trxs){
            TransactionResponse trxRes = buildTransactionResponse(trx);

            trxResponse.add(trxRes);
        }
        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/allTransactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        List<TransactionResponse> trxResponse = new ArrayList<>();
        List<Transaction> trxs = transactionService.getAllTransactions();

        for  (Transaction trx : trxs){
            TransactionResponse trxRes = buildTransactionResponse(trx);

            trxResponse.add(trxRes);
        }
        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"')")
    @GetMapping("/fetch/transactionById/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id){
        TransactionResponse trxRes = buildTransactionResponse(transactionService.getTransactionById(id));

        return ResponseEntity.ok(trxRes);
    }

    private TransactionResponse buildTransactionResponse(Transaction trx){
        return TransactionResponse.builder()
                .transactionId(trx.getId())
                .senderAccountNumber(trx.getSenderAccountNumber())
                .recipientAccountNumber(trx.getRecipientAccountNumber())
                .transactionAmount(trx.getAmount().doubleValue())
                .transactionDate(trx.getTransactionDate())
                .transactionType(trx.getTransactionType())
                .transactionStatus(trx.getTransactionStatus())
                .riskScore(trx.getRiskScore())
                .resolvedAt(trx.getResolvedAt())
                .build();
    }
}
