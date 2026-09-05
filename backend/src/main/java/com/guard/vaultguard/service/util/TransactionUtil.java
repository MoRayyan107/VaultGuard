package com.guard.vaultguard.service.util;

import com.guard.vaultguard.dto.transaction.TransactionRequest;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.exceptions.IllegalTransactionException;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.service.BankService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
// Utility class for transaction-related operations, providing methods to retrieve and save transactions.
public class TransactionUtil {

    private final TransactionRepository transactionRepository;
    private final BankService bankService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction getTransactionByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new IllegalTransactionException("Transaction with reference " + reference + " not found"));
    }

    @Transactional
    public Transaction saveTransaction(TransactionRequest trxReq) {
        Transaction transaction = Transaction.builder()
                .senderAccountNumber(trxReq.getSenderAccountNumber())
                .senderBank(bankService.getBankByCode(trxReq.getSenderBankCode()))
                .amount(trxReq.getAmount())
                .transactionType(trxReq.getTransactionType())
                .senderLocation(trxReq.getSenderLocation())
                .recipientAccountNumber(trxReq.getRecipientAccountNumber())
                .recipientBank(bankService.getBankByCode(trxReq.getRecipientBankCode()))
                .transactionReference(trxReq.getBankTrxReference())
                .transactionDate(LocalDateTime.now()).build();

        return transactionRepository.saveAndFlush(transaction);
    }

}
