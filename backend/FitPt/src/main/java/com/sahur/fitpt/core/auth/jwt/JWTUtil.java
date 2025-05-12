package com.sahur.fitpt.core.auth.jwt;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 */
@Slf4j
@Component
public class JWTUtil {
    private final SecretKey secretKey;                          // JWT 서명에 사용할 비밀키
    private final long accessTokenValidityInMilliseconds;       // 액세스 토큰 유효 기간
    private final long refreshTokenValidityInMilliseconds;      // 리프레시 토큰 유효 기간

    /**
     * 생성자: application.yaml의 설정값으로 초기화
     */
    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.access}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.expiration.refresh}") long refreshTokenValidityInMilliseconds
    ) {
        // 비밀키를 바이트 배열로 변환하여 HMAC-SHA 키 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    /**
     * Access Token 생성
     * @param memberId 회원 ID
     * @param role 회원 역할
     * @return 생성된 액세스 토큰
     */
    public String createAccessToken(Long memberId, String role) {
        return createToken(memberId, role, accessTokenValidityInMilliseconds);
    }

    /**
     * Refresh Token 생성
     * @param memberId 회원 ID
     * @param role 회원 역할
     * @return 생성된 리프레시 토큰
     */
    public String createRefreshToken(Long memberId, String role) {
        return createToken(memberId, role, refreshTokenValidityInMilliseconds);
    }

    /**
     * 토큰 생성 공통 메소드
     * @param memberId 회원 ID
     * @param role 회원 역할
     * @param validityInMilliseconds 토큰 유효 기간
     * @return 생성된 JWT 토큰
     */
    private String createToken(Long memberId, String role, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claim("memberId", memberId)     // 회원 ID 저장
                .claim("role", role)             // 역할 정보 저장
                .setIssuedAt(now)               // 발행 시간
                .setExpiration(validity)         // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)  // HS256 알고리즘으로 서명
                .compact();
    }

    /**
     * 토큰 검증
     * @param token 검증할 토큰
     * @return 토큰 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * Claims 추출
     * @param token JWT 토큰
     * @return 토큰의 Claims
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰이어도 Claims 반환
        }
    }

    /**
     * 토큰에서 회원 ID 추출
     */
    public Long getMemberId(String token) {
        return getClaims(token).get("memberId", Long.class);
    }

    /**
     * 토큰에서 역할 정보 추출
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰의 남은 만료 시간을 반환
     * @param token JWT 토큰
     * @return 남은 만료 시간 (밀리초)
     */
    public long getExpirationTime(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            return Math.max(0, expiration.getTime() - now.getTime());
        } catch (Exception e) {
            return 0;
        }
    }
}