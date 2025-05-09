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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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
        try {
            if (isSkipPath(request.getServletPath())) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = jwtUtil.extractToken(request);
            if (token == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            // 로그아웃된 토큰인지 확인
            String isLogout = redisTemplate.opsForValue().get(token);
            if (isLogout != null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            Long memberId = jwtUtil.getMemberId(token);

            // Security Context에 인증 정보 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            setErrorResponse(response, e);
        }
    }

    private boolean isSkipPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/");
    }

    private void setErrorResponse(HttpServletResponse response, CustomException e) throws IOException {
        ErrorResponse error = new ErrorResponse(e.getErrorCode());
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}