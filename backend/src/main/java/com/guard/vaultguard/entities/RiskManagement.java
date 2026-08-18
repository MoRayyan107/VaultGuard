package com.guard.vaultguard.entities;

import com.guard.vaultguard.entities.enums.RiskLevel;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "risk_management")
public class RiskManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private Transaction transaction;

    @Column(name="risk_score")
    private Double riskScore;

    @Column(name="risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name="transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name="reason")
    private String reason;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

}
