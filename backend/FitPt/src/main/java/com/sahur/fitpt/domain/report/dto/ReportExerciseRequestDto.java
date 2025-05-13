package com.sahur.fitpt.domain.report.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportExerciseRequestDto {
    private String exerciseName;
    private String exerciseAchievement;
    private String exerciseComment;
    private List<Long> activationMuscleId;
}