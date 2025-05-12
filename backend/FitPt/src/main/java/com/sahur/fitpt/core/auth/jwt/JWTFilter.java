package com.sahur.fitpt.core.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahur.fitpt.core.error.ErrorResponse;
import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 인증 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 인증 정보를 설정하는 필터
 */
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;                            // JWT 유틸리티
    private final RedisTemplate<String, String> redisTemplate; // Redis 템플릿
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 필터 로직 구현
     * 모든 요청에 대해 JWT 토큰을 검증하고 인증 정보를 설정
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 인증이 필요없는 경로는 필터 통과
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 요청 헤더에서 JWT 토큰 추출
            String token = jwtUtil.extractToken(request);
            if (token == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN_FORM);
            }

            // Redis에서 로그아웃된 토큰인지 확인
            // 로그아웃된 토큰은 Redis에 저장되어 있음
            String isLoggedOut = redisTemplate.opsForValue().get(token);
            if (isLoggedOut != null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 토큰 유효성 검증
            jwtUtil.validateToken(token);

            // 토큰에서 사용자 정보 추출
            Long memberId = jwtUtil.getMemberId(token);
            String role = jwtUtil.getRole(token);

            // Spring Security 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    memberId,    // Principal: 회원 ID
                    null,       // Credentials: 비밀번호 (JWT에서는 불필요)
                    Collections.singleton(new SimpleGrantedAuthority(role)) // Authorities: 권한 정보
            );

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            // 예외 발생 시 에러 응답 반환
            setErrorResponse(response, e);
        }
    }

    /**
     * 인증이 필요없는 경로인지 확인
     * @param request HTTP 요청
     * @return 인증 제외 여부
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") ||        // 인증 관련 API
                path.startsWith("/swagger-ui") ||     // Swagger UI
                path.startsWith("/v3/api-docs") ||    // API 문서
                path.equals("/swagger-ui.html") ||     // Swagger UI 메인
                path.startsWith("/swagger-resources") ||  // Swagger 리소스
                path.startsWith("/webjars/" +
                        "");          // Webjar 리소스
    }

    /**
     * 에러 응답 설정
     * @param response HTTP 응답
     * @param e 발생한 예외
     */
    private void setErrorResponse(
            HttpServletResponse response,
            CustomException e
    ) throws IOException {
        ErrorResponse error = new ErrorResponse(e.getErrorCode());
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}