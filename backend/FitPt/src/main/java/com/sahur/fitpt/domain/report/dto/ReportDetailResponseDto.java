package com.sahur.fitpt.domain.report.dto;

import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDetailResponseDto {
    private Long reportId;
    private Long memberId;
    private String trainerName;
    private String reportComment;
    private String createdAt;
    private CompositionResponseDto compositionResponseDto;
    private List<ReportExerciseResponseDto> reportExercises;
}