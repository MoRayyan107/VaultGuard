package com.guard.vaultguard.dto;

import com.guard.vaultguard.entities.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransactionDTO {

    @NotBlank
    private String senderAccountNumber;

    private String recipientAccountNumber;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String senderLocation;

    @NotNull
    private TransactionType transactionType;

}
