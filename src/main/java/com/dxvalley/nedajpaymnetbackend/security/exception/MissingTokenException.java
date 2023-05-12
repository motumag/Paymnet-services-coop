package com.dxvalley.nedajpaymnetbackend.security.exception;

public class MissingTokenException extends Exception{
    public MissingTokenException(String message) {
        super(message);
    }
}
