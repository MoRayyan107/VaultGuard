package com.guard.vaultguard.dto.transaction;

import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TransactionResponse {
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private Double transactionAmount;
    private LocalDateTime transactionDate;
    private UUID transactionId;
    private String recipientAccountNumber;
    private String senderAccountNumber;
    private Double riskScore;
    private LocalDateTime resolvedAt;
}
