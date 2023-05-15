package com.dxvalley.nedajpaymnetbackend.payment.nedaj.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler({NumberFormatException.class})
//    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("message", "Invalid request parameter");
//        body.put("details", ex.getMessage());
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "An error occurred");
//        body.put("url", request.getContextPath());
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
