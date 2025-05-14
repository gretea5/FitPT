package com.sahur.fitpt.core.constant;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST
    INVALID_INPUT_NULL_OR_EMPTY_VALUE("Null 값 또는 빈 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("유효하지 않은 입력값입니다", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_FORM("유효하지 않은 토큰 형식입니다", HttpStatus.BAD_REQUEST),
    INVALID_GYM_NAME("잘못된 헬스장 이름입니다.", HttpStatus.BAD_REQUEST),

    // 401 UNAUTHORIZED
    UNAUTHORIZED("인증되지 않은 요청입니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("만료된 토큰입니다", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED),
    MISMATCHED_REFRESH_TOKEN("리프레시 토큰이 일치하지 않습니다", HttpStatus.UNAUTHORIZED),

    // 403 FORBIDDEN
    FORBIDDEN("접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    TRAINER_MEMBER_ACCESS_DENIED("해당 회원에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),

    // 404 NOT_FOUND
    MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    TRAINER_NOT_FOUND("해당 트레이너를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    ADMIN_NOT_FOUND("해당 관리자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMPOSITION_NOT_FOUND("해당 체성분 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND("해당 보고서 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    GYM_NOT_FOUND("검색 결과가 없습니다", HttpStatus.NOT_FOUND),

    // 409 CONFLICT
    DUPLICATE_MEMBER("이미 존재하는 회원입니다", HttpStatus.CONFLICT),
    DUPLICATE_TRAINER("이미 존재하는 트레이너입니다", HttpStatus.CONFLICT),

    // 500 SERVER_ERROR
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    KAKAO_SERVER_ERROR("카카오 서버 연동 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}