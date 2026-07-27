package com.guard.vaultguard.dto.transaction;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionResponse {
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDate;
    private UUID transactionId;
    private String recipientAccountNumber;
    private String senderLocation;
    private String senderAccountNumber;
    private Double riskScore;
    private LocalDateTime resolvedAt;

    // bank sender and receier codes
    private String senderBank;
    private String recipientBank;

    public static TransactionResponse buildTransactionResponse(Transaction trx){
        TransactionResponse.TransactionResponseBuilder resBuilder = TransactionResponse.builder()
                .transactionId(trx.getId())
                .senderAccountNumber(trx.getSenderAccountNumber())
                .senderBank(trx.getSenderBank().getBankName())
                .transactionAmount(trx.getAmount())
                .transactionDate(trx.getTransactionDate())
                .senderLocation(trx.getSenderLocation())
                .transactionType(trx.getTransactionType())
                .transactionStatus(trx.getTransactionStatus())
                .riskScore(trx.getRiskScore())
                .resolvedAt(trx.getResolvedAt());

        // checks if theres a recipient bank and account number
        if (trx.getRecipientBank() != null && trx.getRecipientAccountNumber() != null) {
            resBuilder
                    .recipientBank(trx.getRecipientBank().getBankName())
                    .recipientAccountNumber(trx.getRecipientAccountNumber());
        }

        return resBuilder.build();
    }

    public static List<TransactionResponse> mapToResponse(List<Transaction> trx){
        return trx.stream()
                .map(TransactionResponse::buildTransactionResponse)
                .toList();
    }
}
