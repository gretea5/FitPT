package com.sahur.fitpt.core.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    @JsonProperty("id")
    private Long id;  // 카카오 회원번호

    @JsonProperty("properties")
    private Properties properties;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;  // 카카오 닉네임
    }
}