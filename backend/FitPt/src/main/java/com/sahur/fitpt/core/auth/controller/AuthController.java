package com.sahur.fitpt.core.auth.controller;

import com.sahur.fitpt.core.auth.dto.KakaoLoginRequestDto;
import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.dto.KakaoSignupRequestDto;
import com.sahur.fitpt.core.auth.dto.KakaoSignupResponseDto;
import com.sahur.fitpt.core.auth.dto.TokenRequestDto;
import com.sahur.fitpt.core.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "카카오 소셜 로그인 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/kakao/signup")
    @Operation(summary = "카카오 회원가입", description = "카카오 로그인과 회원가입을 동시에 처리합니다.")
    public ResponseEntity<KakaoSignupResponseDto> kakaoSignup(
            @RequestBody KakaoSignupRequestDto request
    ) {
        return ResponseEntity.ok(
                authService.kakaoSignup(request)
        );
    }

    @Operation(summary = "카카오 로그인", description = "카카오 액세스 토큰으로 로그인을 처리합니다.")
    @PostMapping("/kakao/login")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(
            @RequestBody KakaoLoginRequestDto request
    ) {
        return ResponseEntity.ok(
                authService.kakaoLogin(request.getKakaoAccessToken(), request.getFcmToken())
        );
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @RequestAttribute("memberId") Long memberId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken
    ) {
        String token = bearerToken.substring(7);
        authService.logout(memberId, token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 토큰 세트를 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<KakaoLoginResponseDto> refreshToken(
            @RequestBody TokenRequestDto request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}