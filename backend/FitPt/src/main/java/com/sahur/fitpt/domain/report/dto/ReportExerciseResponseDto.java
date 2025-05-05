package com.sahur.fitpt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExerciseResponseDto {
    private String exerciseName;
    private String exerciseAchievement;
    private String exerciseComment;
    private List<Long> activation_muscle_id;
}