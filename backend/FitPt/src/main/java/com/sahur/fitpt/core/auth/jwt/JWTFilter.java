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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("요청 URI: {}", requestURI);

        if (shouldNotFilter(request)) {
            log.debug("인증이 필요없는 경로입니다: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtUtil.extractToken(request);
            log.debug("추출된 토큰: {}", token);

            if (token == null) {
                log.debug("토큰이 없습니다");
                throw new CustomException(ErrorCode.INVALID_TOKEN_FORM);
            }

            String isLoggedOut = redisTemplate.opsForValue().get(token);
            if (isLoggedOut != null) {
                log.debug("이미 로그아웃된 토큰입니다");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            jwtUtil.validateToken(token);
            log.debug("토큰이 유효합니다");

            Long memberId = jwtUtil.getMemberId(token);
            String role = jwtUtil.getRole(token);
            request.setAttribute("trainerId", memberId);
            log.debug("토큰에서 추출한 정보 - 회원ID: {}, 역할: {}", memberId, role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    memberId,
                    null,
                    Collections.singleton(new SimpleGrantedAuthority(role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("인증 정보가 설정되었습니다 - 회원ID: {}, 역할: {}", memberId, role);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            log.error("인증 처리 중 오류 발생: {}", e.getErrorCode().getMessage());
            setErrorResponse(response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/test/") ||
                path.startsWith("/api/trainers/login") ||
                path.equals("/api/gyms") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/");
    }

    private void setErrorResponse(
            HttpServletResponse response,
            CustomException e
    ) throws IOException {
        ErrorResponse error = new ErrorResponse(e.getErrorCode());
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
        log.error("에러 응답이 설정되었습니다 - 상태: {}, 메시지: {}",
                e.getErrorCode().getHttpStatus().value(),
                e.getErrorCode().getMessage());
    }
}