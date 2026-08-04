package com.guard.vaultguard.dto.transaction;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.entities.RiskManagement;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.enums.RiskLevel;
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
public class TransactionDashboardResponse {

    private UUID transactionId;
    private TransactionType transactionType;
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDate;
    private String senderAccountNumber;
    private String recipientAccountNumber;  // can be nullable by senders self transfer/withrawl
    private String senderLocation;
    private String senderBank;
    private String recipientBank;           // can be nullable

    // from RiskManagement, via join
    private Double riskScore;
    private RiskLevel riskLevel;
    private TransactionStatus transactionStatus;
    private String reason;
    private LocalDateTime createdDate;

    public static TransactionDashboardResponse buildTransactionDashboardResponse(Transaction trx){
        RiskManagement trxRiskManagement = trx.getRiskManagement();

        return TransactionDashboardResponse.builder()
                .transactionId(trx.getId())
                .senderAccountNumber(trx.getSenderAccountNumber())
                .senderBank(trx.getSenderBank() != null ? trx.getSenderBank().getBankName() : null)
                .transactionAmount(trx.getAmount())
                .transactionDate(trx.getTransactionDate())
                .senderLocation(trx.getSenderLocation())
                .transactionType(trx.getTransactionType())
                .recipientAccountNumber(trx.getRecipientAccountNumber() != null ? trx.getRecipientAccountNumber() : null)
                .recipientBank(trx.getRecipientBank() != null ? trx.getRecipientBank().getBankName() : null)

                // risk management
                .riskLevel(trxRiskManagement.getRiskLevel())
                .riskScore(trxRiskManagement.getRiskScore())
                .reason(trxRiskManagement.getReason())
                .createdDate(trxRiskManagement.getCreatedAt())
                .build();
    }

    public static List<TransactionDashboardResponse> mapToResponse(List<Transaction> trx){
        return trx.stream()
                .map(TransactionDashboardResponse::buildTransactionDashboardResponse)
                .toList();
    }
}
