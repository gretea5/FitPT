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

        // OpenAPI 설정
        return new OpenAPI()
                .info(new Info()
                        .title("FitPt API")           // API 제목
                        .description("FitPt API 문서") // API 설명
                        .version("1.0"))              // API 버전
                .components(new Components()) // 보안 스키마 추가
                .servers(Arrays.asList(               // 서버 목록 추가
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}