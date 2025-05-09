package com.sahur.fitpt.core.auth.service;

import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.dto.KakaoUserInfo;
import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.core.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final JWTUtil jwtUtil;

    public KakaoLoginResponseDto kakaoLogin(String kakaoAccessToken) {
        KakaoUserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);

        String accessToken = jwtUtil.createAccessToken(userInfo.getId());
        String refreshToken = jwtUtil.createRefreshToken(userInfo.getId());

        redisTemplate.opsForValue()
                .set("RT:" + userInfo.getId(), refreshToken,
                        7, TimeUnit.DAYS);

        return KakaoLoginResponseDto.builder()
                .memberId(userInfo.getId())
                .memberName(userInfo.getProperties().getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String token) {
        String accessToken = token.substring(7);
        Long kakaoId = jwtUtil.getMemberId(accessToken);

        redisTemplate.delete("RT:" + kakaoId);

        long expiration = jwtUtil.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public KakaoLoginResponseDto refresh(String refreshToken) {
        Long kakaoId = jwtUtil.getMemberId(refreshToken);

        String storedToken = redisTemplate.opsForValue().get("RT:" + kakaoId);
        if (!refreshToken.equals(storedToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.createAccessToken(kakaoId);

        return KakaoLoginResponseDto.builder()
                .memberId(kakaoId)
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfo.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}