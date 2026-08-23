package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.ProcessTrxResponse;
import com.guard.vaultguard.dto.transaction.TransactionDashboardResponse;
import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.exceptions.BankCodeNotFoundException;
import com.guard.vaultguard.exceptions.DuplicateTransactionException;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
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
        try {
            Transaction trx = transactionService.processTransaction(trxReq);
            ProcessTrxResponse trxResponse = ProcessTrxResponse.success(trx);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(trxResponse);
        }
        catch (IllegalTransactionException | BankCodeNotFoundException  e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProcessTrxResponse.failure(trxReq, e.getMessage()));
        }
        catch (DuplicateTransactionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ProcessTrxResponse.failure(trxReq, e.getMessage()));
        }
    }

    // DEPRECATED: redundant endpoint — findAll transactions endpoint will cover flagged status id's etc. in future
    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/flaggedTransactions")
    public ResponseEntity<List<TransactionDashboardResponse>> getFlaggedTransactions(){
        List<Transaction> trxs = transactionService.getFlaggedTransactions();

        List<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    // DEPRECATED: redundant endpoint — high risk transactions are already covered by flagged transactions
    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/highRiskTransactions")
    public ResponseEntity<List<TransactionDashboardResponse>> getAllHighRiskTransactions(){
        List<Transaction> trxs = transactionService.getAllHighRiskTransactions();

        List<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/allTransactions")
    public ResponseEntity<Page<TransactionDashboardResponse>> getAllTransactions(
            @RequestParam(required = false) String senderBankCode,
            Pageable pageable)
    {
        Page<Transaction> trxs = transactionService.getAllTransactions(senderBankCode, pageable);

        Page<TransactionDashboardResponse> trxResponse = TransactionDashboardResponse.mapToResponse(trxs);

        return ResponseEntity.ok(trxResponse);
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"')")
    @GetMapping("/fetch/transactionById/{id}")
    public ResponseEntity<TransactionDashboardResponse> getTransactionById(@PathVariable UUID id){
        TransactionDashboardResponse trxRes = TransactionDashboardResponse.buildTransactionDashboardResponse(transactionService.getTransactionById(id));

        return ResponseEntity.ok(trxRes);
    }


}
