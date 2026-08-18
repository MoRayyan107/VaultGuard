package com.guard.vaultguard.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Entity
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bank_id")
    private UUID bankId;

    @Column(name = "bank_name", nullable = false, unique = true)
    private String bankName;

    @Column(name = "bank_code", unique = true)
    private String bankCode;

    // meaning if the bank is registerd with us or not
    @Column(nullable = false)
    private boolean active;

}
