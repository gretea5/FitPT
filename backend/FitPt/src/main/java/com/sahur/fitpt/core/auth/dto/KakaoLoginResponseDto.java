package com.sahur.fitpt.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "카카오 로그인 응답 DTO")
public class KakaoLoginResponseDto {
    @Schema(description = "회원 ID")
    private Long memberId;

    @Schema(description = "회원 이름")
    private String memberName;

    @Schema(description = "회원 성별")
    private String memberGender;

    @Schema(description = "회원 생년월일")
    private LocalDate memberBirth;

    @Schema(description = "회원 키")
    private Float memberHeight;

    @Schema(description = "회원 체중")
    private Float memberWeight;

    @Schema(description = "관리자 ID")
    private Long adminId;

    @Schema(description = "헬스장 이름")
    private String gymName;

    @Schema(description = "FCM 토큰")
    private String fcmToken;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;
}