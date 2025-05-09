package com.sahur.fitpt.core.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequestDto {
    private String kakaoAccessToken;
}