package com.sahur.fitpt.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Swagger(OpenAPI) 설정 클래스
 * API 문서화를 위한 설정을 담당
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        // JWT 인증 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)        // HTTP 인증 방식
                .scheme("bearer")                      // Bearer 인증
                .bearerFormat("JWT")                   // JWT 형식
                .in(SecurityScheme.In.HEADER)          // 헤더에 포함
                .name("Authorization");                // 헤더 이름

        // 보안 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");               // 모든 API에 JWT 인증 필요

        // OpenAPI 설정
        return new OpenAPI()
                .info(new Info()
                        .title("FitPt API")           // API 제목
                        .description("FitPt API 문서") // API 설명
                        .version("1.0"))              // API 버전
                .addSecurityItem(securityRequirement) // 보안 요구사항 추가
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme)) // 보안 스키마 추가
                .servers(Arrays.asList(               // 서버 목록 추가
                        new Server().url("http://localhost:8080").description("Local Server"),
                        new Server().url("http://k12s208.p.ssafy.io:9090").description("Production Server")
                ));
    }
}