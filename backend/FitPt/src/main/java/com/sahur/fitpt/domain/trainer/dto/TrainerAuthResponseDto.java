package com.sahur.fitpt.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "트레이너 인증 응답 DTO")
public class TrainerAuthResponseDto {
    @Schema(description = "트레이너 ID", example = "1")
    private Long trainerId;

    @Schema(description = "트레이너 이름", example = "홍길동")
    private String trainerName;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;
}