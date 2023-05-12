package com.dxvalley.nedajpaymnetbackend.payment.equb;

public class EqubCustomException extends Exception{
    private int status;
    private String message;

    public EqubCustomException(int status, String message) {
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
