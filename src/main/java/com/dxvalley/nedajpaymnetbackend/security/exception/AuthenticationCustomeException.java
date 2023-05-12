package com.dxvalley.nedajpaymnetbackend.security.exception;

public class AuthenticationCustomeException extends Exception {
    private int status;
    private String message;

    public AuthenticationCustomeException(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
