package com.guard.vaultguard.dto.bank;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BankRequest {

    @NotBlank
    private String bankCode;

    @NotBlank
    private String bankName;

}
