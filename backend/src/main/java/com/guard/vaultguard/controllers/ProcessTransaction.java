package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.transaction.ProcessTrxResponse;
import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.exceptions.BankCodeNotFoundException;
import com.guard.vaultguard.exceptions.BankNotActiveException;
import com.guard.vaultguard.exceptions.DuplicateTransactionException;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/proccess/fraudDetection")
@AllArgsConstructor
public class ProcessTransaction {

    private final TransactionService transactionService;

    @PostMapping("/transaction")
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
        catch (IllegalTransactionException | BankCodeNotFoundException | BankNotActiveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProcessTrxResponse.failure(trxReq, e.getMessage()));
        }
        catch (DuplicateTransactionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ProcessTrxResponse.failure(trxReq, e.getMessage()));
        }
    }

}
