package com.okanetransfer.exception;

public class InsufficientCashBalanceException extends RuntimeException {
    public InsufficientCashBalanceException(String message) {
        super(message);
    }
}
