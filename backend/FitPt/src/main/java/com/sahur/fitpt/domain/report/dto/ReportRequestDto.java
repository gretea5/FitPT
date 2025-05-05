package com.sahur.fitpt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Long memberId;
    private Long compositionLogId;
    private Long trainerId;
    private String reportComment;
    private List<ReportExerciseRequestDto> reportExercises;
}
