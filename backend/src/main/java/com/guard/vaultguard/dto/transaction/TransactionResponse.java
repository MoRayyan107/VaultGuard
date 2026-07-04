package com.guard.vaultguard.dto.transaction;

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
    private String senderAccountNumber;
    private Double riskScore;
    private LocalDateTime resolvedAt;

    public static TransactionResponse buildTransactionResponse(Transaction trx){
        return TransactionResponse.builder()
                .transactionId(trx.getId())
                .senderAccountNumber(trx.getSenderAccountNumber())
                .recipientAccountNumber(trx.getRecipientAccountNumber())
                .transactionAmount(trx.getAmount())
                .transactionDate(trx.getTransactionDate())
                .transactionType(trx.getTransactionType())
                .transactionStatus(trx.getTransactionStatus())
                .riskScore(trx.getRiskScore())
                .resolvedAt(trx.getResolvedAt())
                .build();
    }

    public static List<TransactionResponse> mapToResponse(List<Transaction> trx){
        return trx.stream()
                .map(TransactionResponse::buildTransactionResponse)
                .toList();
    }
}
