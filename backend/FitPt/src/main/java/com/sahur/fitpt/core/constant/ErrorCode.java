package com.sahur.fitpt.core.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE("유효하지 않은 입력값입니다", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_FORM("유효하지 않은 토큰 형식입니다", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("인증되지 않은 요청입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_ACCESS_TOKEN("액세스 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("리프레시 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),

    TRAINER_NOT_FOUND("존재하지 않는 트레이너입니다", HttpStatus.NOT_FOUND);

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
