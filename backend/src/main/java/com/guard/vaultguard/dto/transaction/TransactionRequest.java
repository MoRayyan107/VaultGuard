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

    @NotBlank
    private String senderBankCode;

    // sender bank reference acting for idempotency key (always unique)
    @NotBlank
    private String bankTrxReference;

    private String recipientAccountNumber;

    private String recipientBankCode;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String senderLocation;

    @NotNull
    private TransactionType transactionType;

}
