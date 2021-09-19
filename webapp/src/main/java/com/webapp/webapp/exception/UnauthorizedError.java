package com.webapp.webapp.exception;

/**
 * Exception is thrown when the credentials are invalid or
 * the Email doesn't not exist.
 */
public class UnauthorizedError extends RuntimeException {
    public UnauthorizedError(String message) {
        super(message);
    }
}
