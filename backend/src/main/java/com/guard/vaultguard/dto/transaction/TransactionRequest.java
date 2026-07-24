package com.guard.vaultguard.dto.transaction;

import com.guard.vaultguard.entities.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionRequest {

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
