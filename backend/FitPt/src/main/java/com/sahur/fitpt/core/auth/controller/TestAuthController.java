package com.sahur.fitpt.core.auth.controller;

import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import com.sahur.fitpt.core.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/token")
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Tag(name = "Test", description = "테스트용 토큰 발급 API")
public class TestAuthController {
    private final JWTUtil jwtUtil;

    @Data
    public static class TokenResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String role;
    }

    @GetMapping("/trainer/{trainerId}")
    @Operation(summary = "트레이너 테스트 토큰 발급", description = "테스트용 트레이너 토큰을 발급합니다.")
    public ResponseEntity<TokenResponse> issueTrainerToken(@PathVariable Long trainerId) {
        String accessToken = jwtUtil.createAccessToken(trainerId, Role.TRAINER.getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                refreshToken,
                Role.TRAINER.getTitle()
        ));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원 테스트 토큰 발급", description = "테스트용 회원 토큰을 발급합니다.")
    public ResponseEntity<TokenResponse> issueMemberToken(@PathVariable Long memberId) {
        String accessToken = jwtUtil.createAccessToken(memberId, Role.MEMBER.getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                refreshToken,
                Role.MEMBER.getTitle()
        ));
    }
}