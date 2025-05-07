package com.sahur.fitpt.domain.schedule.dto;

import com.sahur.fitpt.db.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "PT 일정 응답 DTO")
public class ScheduleResponseDto {
    @Schema(description = "일정 ID", example = "1")
    private Long scheduleId;

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

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .memberId(schedule.getMember().getMemberId())
                .trainerId(schedule.getTrainer().getTrainerId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .scheduleContent(schedule.getScheduleContent())
                .build();
    }
}