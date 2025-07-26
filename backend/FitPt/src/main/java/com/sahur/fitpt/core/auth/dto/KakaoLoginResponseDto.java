package com.sahur.fitpt.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카카오 로그인 응답 DTO")
public class KakaoLoginResponseDto {
    @Schema(description = "회원 ID")
    private Long memberId;

    @Schema(description = "회원 이름")
    private String memberName;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;
}
