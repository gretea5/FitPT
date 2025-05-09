package com.sahur.fitpt.core.auth.controller;

import com.sahur.fitpt.core.auth.dto.KakaoLoginRequestDto;
import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.service.AuthService;
import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpirationMillis;

    @PostMapping("/kakao")
    @Operation(summary = "카카오 로그인", description = "카카오 액세스 토큰으로 로그인을 처리합니다.")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto request) {
        return ResponseEntity.ok(authService.kakaoLogin(request.getKakaoAccessToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    public ResponseEntity<KakaoLoginResponseDto> refresh(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/test-token")
    @Operation(summary = "테스트용 토큰 발급", description = "개발 테스트용 토큰을 발급하고, 발급된 리프레쉬 토큰을 Redis에 저장합니다.")
    public ResponseEntity<Map<String, String>> createTestToken() {
        Long testMemberId = 1L; // 테스트용 사용자 ID

        String accessToken = jwtUtil.createAccessToken(testMemberId);
        String refreshToken = jwtUtil.createRefreshToken(testMemberId);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(
                "RT:" + testMemberId,
                refreshToken,
                refreshTokenExpirationMillis,
                TimeUnit.MILLISECONDS
        );

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }
}