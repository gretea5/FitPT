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
    // 상수 정의
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MEMBER_ID_CLAIM = "memberId";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    // 생성자: yaml 파일 설정값으로 초기화
    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.access}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.expiration.refresh}") long refreshTokenValidityInMilliseconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    // Access Token 생성: 회원 ID와 role 정보 포함
    public String createAccessToken(Long memberId, String role) {
        return createToken(memberId, role, accessTokenValidityInMilliseconds);
    }

    // Refresh Token 생성: 만료 시간만 포함
    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 생성 공통 메소드
    private String createToken(Long memberId, String role, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claim(MEMBER_ID_CLAIM, memberId)
                .claim(ROLE_CLAIM, role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 파싱 공통 메소드: 예외 처리 통합
    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        parseToken(token);
        return true;
    }

    // 토큰에서 Claims 추출
    public Claims getClaims(String token) {
        return parseToken(token);
    }

    // 토큰에서 회원 ID 추출
    public Long getMemberId(String token) {
        return getClaims(token).get(MEMBER_ID_CLAIM, Long.class);
    }

    // 토큰에서 역할 정보 추출
    public String getRole(String token) {
        return getClaims(token).get(ROLE_CLAIM, String.class);
    }

    // HTTP 요청 헤더에서 토큰 추출
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 토큰의 남은 만료 시간 계산
    public long getExpirationTime(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        Date now = new Date();
        return Math.max(0, expiration.getTime() - now.getTime());
    }
}