package com.sahur.fitpt.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "PT 일정 생성 요청 DTO")
public class ScheduleRequestDto {
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "트레이너 ID", example = "100")
    private Long trainerId;

    @Schema(description = "일정 시작 시간", example = "2025-05-01T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간", example = "2025-05-01T11:00:00")
    private LocalDateTime endTime;

    @Schema(description = "일정 내용", example = "상체 위주 운동")
    private String scheduleContent;
}