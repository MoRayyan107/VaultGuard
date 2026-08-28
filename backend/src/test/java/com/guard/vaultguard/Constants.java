package com.guard.vaultguard;

import com.guard.vaultguard.entities.enums.TransactionType;

import java.math.BigDecimal;

public class Constants {

    public Constants() {
        throw new RuntimeException("Cannot instantiate Constants class");
    }

    // sender accccount details
    public static final String SENDER_ACCOUNT_NUMBER = "ACC-001";
    public static final String SENDER_BANK_CODE = "BK002";
    public static final String SENDER_LOCATION = "New York";

    // receiver account details
    public static final String RECIPIENT_ACCOUNT_NUMBER = "ACC-002";
    public static final String RECIPIENT_BANK_CODE = "BK001";

    // transaction amount
    public static final BigDecimal NORMAL_TRANSACTION_AMOUNT = new BigDecimal("1000.00");

    // bankReference
    public static final String BANK_REFERENCE = "BANK-REF-001";

    // transaction Type
    public static final TransactionType TRANSFER_TRANSACTION_TYPE = TransactionType.TRANSFER;
    public static final TransactionType DEPOSIT_TRANSACTION_TYPE = TransactionType.DEPOSIT;
    public static final TransactionType WITHDRAWAL_TRANSACTION_TYPE = TransactionType.WITHDRAW;

    // risk score
    public static final double RISK_SCORE_0_7 = 0.7;
    public static final double RISK_SCORE_0_4 = 0.4;


}
