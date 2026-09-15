package com.guard.vaultguard.exceptions;

public class IllegalTransactionTypeException extends RuntimeException {
    public IllegalTransactionTypeException(String message) {
        super(message);
    }
}
