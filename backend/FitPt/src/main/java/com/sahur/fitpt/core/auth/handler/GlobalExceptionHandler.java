package com.sahur.fitpt.core.auth.handler;

import com.sahur.fitpt.core.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Void> handleCustomException(CustomException e) {
        return new ResponseEntity<>(e.getHttpStatus());
    }
}
