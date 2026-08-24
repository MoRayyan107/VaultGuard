package com.guard.vaultguard.exceptions;

public class IllegalTransactionStatusException extends RuntimeException {
    public IllegalTransactionStatusException(String message) {
        super(message);
    }
}
