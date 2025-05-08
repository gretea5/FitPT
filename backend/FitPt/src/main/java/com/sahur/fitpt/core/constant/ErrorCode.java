package com.sahur.fitpt.core.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_NULL_OR_EMPTY_VALUE("Null 값 또는 빈 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("유효하지 않은 입력값입니다", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_FORM("유효하지 않은 토큰 형식입니다", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("인증되지 않은 요청입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_ACCESS_TOKEN("액세스 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("리프레시 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED),

    ADMIN_NOT_FOUND("해당 관리자 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    TRAINER_NOT_FOUND("해당 트레이너를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMPOSITION_NOT_FOUND("해당 체성분 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND("해당 보고서 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
