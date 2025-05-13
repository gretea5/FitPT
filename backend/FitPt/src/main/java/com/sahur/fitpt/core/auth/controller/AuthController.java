package com.sahur.fitpt.core.auth.controller;

import com.sahur.fitpt.core.auth.dto.KakaoLoginRequestDto;
import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 카카오 로그인 API
     * 카카오 SDK로부터 받은 액세스 토큰으로 로그인 처리
     */
    @Operation(summary = "카카오 로그인", description = "카카오 액세스 토큰으로 로그인/회원가입을 처리합니다.")
    @PostMapping("/kakao/login")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(
            @RequestBody KakaoLoginRequestDto request
    ) {
        return ResponseEntity.ok(
                authService.kakaoLogin(request.getKakaoAccessToken())
        );
    }

    /**
     * 로그아웃 API
     * 현재 사용 중인 토큰을 무효화
     */
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

    /**
     * 토큰 재발급 API
     * Refresh 토큰으로 새로운 Access 토큰 발급
     */
    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/reissue")
    public ResponseEntity<String> reissueAccessToken(
            @Parameter(hidden = true) @RequestAttribute("memberId") Long memberId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken
    ) {
        String refreshToken = bearerToken.substring(7);
        String newAccessToken = authService.reissueAccessToken(memberId, refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}