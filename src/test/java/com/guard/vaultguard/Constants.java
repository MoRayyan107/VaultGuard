package com.guard.vaultguard;

import java.math.BigDecimal;

public class Constants {

    public Constants() {
        throw new RuntimeException("Cannot instantiate Constants class");
    }

    public static final String SENDER_ACCOUNT_NUMBER = "ACC-001";
    public static final String RECEIVER_ACCOUNT_NUMBER = "ACC-002";
    public static final String SENDER_LOCATION = "New York";

    public static final BigDecimal AMOUNT_GREATER_50_THOUSAND = BigDecimal.valueOf(50_001);
    public static final BigDecimal AMOUNT_GREATER_100_THOUSAND = BigDecimal.valueOf(100_001);
    public static final BigDecimal AMOUNT_LESSER_50_THOUSAND = BigDecimal.valueOf(10_000);
    public static final BigDecimal AMOUNT_NEGATIVE = BigDecimal.valueOf(-10_000);


}
