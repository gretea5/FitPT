package com.sahur.fitpt.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}