package com.dxvalley.nedajpaymnetbackend.payment.nedaj;

public class NedajCustomException extends Exception {
    private int status;
    private String message;

    public NedajCustomException(int status, String message) {
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
