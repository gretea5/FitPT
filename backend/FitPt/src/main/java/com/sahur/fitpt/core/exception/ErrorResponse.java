package com.sahur.fitpt.core.exception;

import com.sahur.fitpt.core.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
    }
}