package com.payflow.app.exception;

public class AccountAccessException extends RuntimeException {
    public AccountAccessException(String message) {
        super(message);
    }
}
