package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.ProcessTrxResponse;
import com.guard.vaultguard.dto.transaction.TransactionDashboardResponse;
import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.exceptions.BankCodeNotFoundException;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ProcessTrxResponse> processTransaction(
            @Valid @RequestBody TransactionRequest trxReq
    )
    {
        Transaction trx = null;
        try {
            trx = transactionService.processTransaction(trxReq);
            ProcessTrxResponse trxResponse = ProcessTrxResponse.success(trx);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(trxResponse);
        }
        catch (IllegalTransactionException | BankCodeNotFoundException e) {
            assert trx  != null;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProcessTrxResponse.failure(trx, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/flaggedTransactions")
    public ResponseEntity<List<TransactionDashboardResponse>> getFlaggedTransactions(){
        List<Transaction> trxs = transactionService.getFlaggedTransactions();

        List<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/highRiskTransactions")
    public ResponseEntity<List<TransactionDashboardResponse>> getAllHighRiskTransactions(){
        List<Transaction> trxs = transactionService.getAllHighRiskTransactions();

        List<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/allTransactions")
    public ResponseEntity<List<TransactionDashboardResponse>> getAllTransactions(){
        List<Transaction> trxs = transactionService.getAllTransactions();

        List<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"')")
    @GetMapping("/fetch/transactionById/{id}")
    public ResponseEntity<TransactionDashboardResponse> getTransactionById(@PathVariable UUID id){
        TransactionDashboardResponse trxRes = TransactionDashboardResponse.buildTransactionDashboardResponse(transactionService.getTransactionById(id));

        return ResponseEntity.ok(trxRes);
    }


}
