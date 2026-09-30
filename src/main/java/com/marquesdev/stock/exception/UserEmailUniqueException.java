package com.marquesdev.stock.exception;

public class UserEmailUniqueException extends RuntimeException {
    public UserEmailUniqueException(String message) {
        super(message);
    }
}
