package com.sahur.fitpt.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 재발급 요청 DTO")
public class TokenRequestDto {
    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;
}