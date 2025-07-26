package com.sahur.fitpt.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "카카오 회원가입 요청 DTO")
public class KakaoSignupRequestDto {
    @Schema(description = "카카오 액세스 토큰", example = "kakao_access_token_example")
    private String kakaoAccessToken;

    @Schema(description = "회원 이름(소셜닉네임)")
    private String memberName;

    @Schema(description = "성별", example = "남성")
    private String memberGender;

    @Schema(description = "생년월일", example = "YYYY-MM-DD")
    private LocalDate memberBirth;

    @Schema(description = "키")
    private Float memberHeight;

    @Schema(description = "체중")
    private Float memberWeight;

    @Schema(description = "관리자 ID")
    private Long adminId;

    @Schema(description = "FCM 토큰")
    private String fcmToken;
}