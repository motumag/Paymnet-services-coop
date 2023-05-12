package com.dxvalley.nedajpaymnetbackend.security.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String error = bindingResult.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse(e.getMessage());
//        return ResponseEntity.status(e.getStatusCode()).body(error);
        return ResponseEntity.status(e.getStatusCode())
                .header("Content-Type", "application/json")
                .body(error);
    }
    @ExceptionHandler(value = {SQLException.class})
    @ResponseBody
    public ResponseEntity<String> handleSQLException(SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while accessing the database: " + e.getMessage());
    }

    @ExceptionHandler(value = {SecurityException.class, AccessDeniedException.class})
    @ResponseBody
    public ResponseEntity<String> handleSecurityException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to access this resource");
    }
    @ExceptionHandler(value = {IOException.class})
    @ResponseBody
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An I/O error occurred: " + e.getMessage());
    }
    @ExceptionHandler(value = {InterruptedException.class})
    @ResponseBody
    public ResponseEntity<String> handleInterruptedException(InterruptedException e) {
        Thread.currentThread().interrupt(); // reset the interrupt flag
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The operation was interrupted: " + e.getMessage());
    }
    @ExceptionHandler(value = {ExpiredJwtException.class})
    @ResponseBody
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The authentication token has expired: " + e.getMessage());
    }
}
