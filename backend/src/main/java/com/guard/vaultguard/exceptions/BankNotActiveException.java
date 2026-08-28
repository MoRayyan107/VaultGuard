package com.guard.vaultguard.exceptions;

public class BankNotActiveException extends RuntimeException {
    public BankNotActiveException(String message) {
        super(message);
    }
}
