package com.sahur.fitpt.core.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    private Long id;
    private Properties properties;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
    }
}