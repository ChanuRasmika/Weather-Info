package org.example.backend.exception;

public class MfaException extends RuntimeException {
    public MfaException(String message) {
        super(message);
    }
}