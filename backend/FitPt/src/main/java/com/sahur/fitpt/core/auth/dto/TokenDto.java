package com.sahur.fitpt.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "토큰 정보 DTO")
public class TokenDto {
    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;

    @Schema(description = "액세스 토큰 만료 시간 (밀리초)")
    private Long accessTokenExpiresIn;
}