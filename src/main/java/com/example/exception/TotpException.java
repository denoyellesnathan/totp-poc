package com.example.exception;

public class TotpException extends RuntimeException {
    public TotpException() {
    }

    public TotpException(String message) {
        super(message);
    }

    public TotpException(String message, Throwable cause) {
        super(message, cause);
    }
}
