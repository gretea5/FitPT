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

    private String memberName;
    private String memberGender;
    private LocalDate memberBirth;
    private Float memberHeight;
    private Float memberWeight;
    private Long adminId;
    private String gymName;
    private String fcmToken;
}