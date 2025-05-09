package com.sahur.fitpt.core.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponseDto {
    private Long memberId;
    private String memberName;
    private String accessToken;
    private String refreshToken;
}