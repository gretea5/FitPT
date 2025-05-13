package com.sahur.fitpt.core.config;

import com.sahur.fitpt.core.auth.jwt.JWTFilter;
import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Spring Security 설정 클래스
 * 보안 관련 설정을 담당
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)    // CSRF 보호 비활성화 (REST API이므로)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .sessionManagement(session ->             // 세션 관리 설정
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로 설정
                        .requestMatchers(
                                "/api/auth/**",          // 인증 관련 API
                                "/api/trainers/login",
                                "/api/test/**",
                                "/swagger-ui/**",        // Swagger UI
                                "/swagger/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs/**",       // OpenAPI 문서
                                "/v3/api-docs",          // 슬래시 없는 버전 추가
                                "/v3/api-docs/",         // 슬래시 있는 버전 추가
                                "/swagger-resources/**",  // Swagger 리소스
                                "/swagger-resources",     // 슬래시 없는 버전 추가
                                "/configuration/ui",      // 추가 설정 경로
                                "/configuration/security", // 추가 설정 경로
                                "/webjars/**",            // Webjar 리소스
                                "/favicon.ico"
                        ).permitAll()
                        // 역할별 접근 권한 설정
                        .requestMatchers("/api/trainers").hasRole("TRAINER")
                        .requestMatchers("/api/trainers/**").hasRole("TRAINER")// 트레이너 전용
                        .requestMatchers("/api/members").hasRole("TRAINER") // 목록 조회는 트레이너만
                        .requestMatchers("/api/members/**").hasAnyRole("MEMBER", "TRAINER")  // 회원과 트레이너 접근 가능
                        .anyRequest().authenticated()    // 그 외 요청은 인증 필요
                )
                // JWT 필터 추가
                .addFilterBefore(
                        new JWTFilter(jwtUtil, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS 설정
     * Cross-Origin Resource Sharing 정책 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("*"));     // 모든 출처 허용
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메서드
            config.setAllowedHeaders(List.of("*"));     // 모든 헤더 허용
            config.setMaxAge(3600L);                    // pre-flight 요청 캐시 시간 (1시간)
            return config;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}