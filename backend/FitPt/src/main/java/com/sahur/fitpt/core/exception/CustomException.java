package com.sahur.fitpt.core.exception;

import lombok.Getter;
import com.sahur.fitpt.core.constant.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
