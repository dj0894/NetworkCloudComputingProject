package com.webapp.webapp.exception;

public class BadRequestException extends RuntimeException {
    private String field;

    public BadRequestException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return this.field;
    }

}
