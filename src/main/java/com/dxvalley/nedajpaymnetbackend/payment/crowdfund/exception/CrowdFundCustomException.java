package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.exception;

public class CrowdFundCustomException extends Exception {
    private int status;
    private String message;

    public CrowdFundCustomException(int status, String message) {
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
