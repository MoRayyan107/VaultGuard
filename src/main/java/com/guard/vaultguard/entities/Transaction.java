package com.guard.vaultguard.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;

import lombok.*;


@Entity(name = "transactions")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String senderAccountNumber;

    @Column(nullable = false)
    private String senderLocation;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 100)
    private String recipientAccountNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column
    private double riskScore;

    @Column
    private LocalDateTime resolvedAt;
}
