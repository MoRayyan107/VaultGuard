package com.guard.vaultguard.entities;

import com.guard.vaultguard.entities.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity(name = "transactions")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
