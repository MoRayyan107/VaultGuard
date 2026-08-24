package com.guard.vaultguard.exceptions;

public class BankCodeNotFoundException extends RuntimeException {
    public BankCodeNotFoundException(String message) {
        super(message);
    }
}
