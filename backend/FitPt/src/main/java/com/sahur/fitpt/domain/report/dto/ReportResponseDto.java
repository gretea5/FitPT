package com.sahur.fitpt.domain.report.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {
    private Long memberId;
    private Long compositionLogId;
    private Long trainerId;
    private String reportComment;
    private List<ReportExerciseResponseDto> reportExercises;
}
