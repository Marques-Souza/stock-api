package com.marquesdev.stock.exception;

public class EmailUniqueException extends RuntimeException {
    public EmailUniqueException(String message) {
        super(message);
    }
}
