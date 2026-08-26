package com.guard.vaultguard.dto.transaction;

import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class ProcessTrxResponse {

    private UUID transactionId;
    private TransactionStatus status;       // e.g. ACCEPTED / REJECTED — NOT the risk-derived status
    private String message;                 // "Transaction accepted for processing" / failure reason
    private LocalDateTime transactionDate;
    private BigDecimal transactionAmount;
    private String senderAccountNumber;
    private String recipientAccountNumber;  // nullable, same guard as before
    private String senderBank;
    private String recipientBank;

    // when the trx passes the fraud detection
    public static ProcessTrxResponse success(Transaction trx){
        return ProcessTrxResponse.builder()
                .transactionId(trx.getId())
                .status(trx.getRiskManagement() != null ? trx.getRiskManagement().getTransactionStatus() : null)
                .message("Transaction completed successfully")
                .transactionDate(trx.getTransactionDate())
                .transactionAmount(trx.getAmount())
                .senderAccountNumber(trx.getSenderAccountNumber())
                .recipientAccountNumber(trx.getRecipientAccountNumber() != null ? trx.getRecipientAccountNumber() : null)
                .senderBank(trx.getSenderBank().getBankName())
                .recipientBank(trx.getRecipientBank() != null ? trx.getRecipientBank().getBankName() : null)
                .build();
    }

    public static ProcessTrxResponse failure(TransactionRequest trxReq, String message){
        return ProcessTrxResponse.builder()
                .status(TransactionStatus.FAILED)
                .message(message)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .transactionAmount(trxReq.getAmount())
                .senderAccountNumber(trxReq.getSenderAccountNumber())
                .recipientAccountNumber(trxReq.getRecipientAccountNumber())
                .senderBank(trxReq.getSenderBankCode())
                .recipientBank(trxReq.getRecipientBankCode())
                .build();
    }
}
