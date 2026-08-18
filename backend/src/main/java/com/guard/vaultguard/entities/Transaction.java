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
@ToString
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String senderAccountNumber;

    // Fetch type lazy since we need evaluate the client bank to be active
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_code", nullable = false)
    private Bank senderBank;

    @Column(nullable = false)
    private String senderLocation;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 100)
    private String recipientAccountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_bank_code")
    private Bank recipientBank;

    @OneToOne(mappedBy = "transaction",fetch = FetchType.LAZY)
    private RiskManagement riskManagement;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
