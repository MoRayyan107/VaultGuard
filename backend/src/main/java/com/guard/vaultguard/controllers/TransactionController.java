package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.TransactionDashboardResponse;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

import static com.guard.vaultguard.config.Constants.ROLE_ANALYST;
import static com.guard.vaultguard.config.Constants.ROLE_MANAGER;


@RestController
@RequestMapping("api/v1/dashboard")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAnyRole('"+ROLE_MANAGER+"','"+ROLE_ANALYST+"')")
    @GetMapping("/fetch/allTransactions")
    public ResponseEntity<Page<TransactionDashboardResponse>> getAllTransactions(
            @RequestParam(required = false) String senderBankCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            Pageable pageable)
    {
        Page<Transaction> trxs = transactionService.getAllTransactions(senderBankCode, status,
                                                                       riskLevel, transactionType,
                                                                       dateFrom, dateTo, pageable);

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
