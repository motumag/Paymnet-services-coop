package com.dxvalley.nedajpaymnetbackend.otpservices;

public class OtpCustomeException extends Exception{
    private int status;
    private String message;

    public OtpCustomeException(int status, String message) {
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
