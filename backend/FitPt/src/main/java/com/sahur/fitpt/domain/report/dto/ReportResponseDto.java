package com.sahur.fitpt.domain.report.dto;

import com.sahur.fitpt.db.entity.Report;
import com.sahur.fitpt.db.entity.ReportExercise;
import com.sahur.fitpt.db.entity.WorkoutMuscle;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {
    private Long reportId;
    private Long memberId;
    private Long compositionLogId;
    private String trainerName;
    private String reportComment;
    private String createdAt;
    private List<ReportExerciseResponseDto> reportExercises;

    public static ReportResponseDto toDto(Report report) {
        List<ReportExerciseResponseDto> exerciseDtos = new ArrayList<>();

        for (ReportExercise exercise : report.getReportExercises()) {
            List<Long> muscleIds = exercise.getWorkoutMuscles().stream()
                    .map(WorkoutMuscle::getActivationMuscleId)
                    .collect(Collectors.toList());

            ReportExerciseResponseDto exerciseDto = ReportExerciseResponseDto.builder()
                    .exerciseName(exercise.getExerciseName())
                    .exerciseAchievement(exercise.getExerciseAchievement())
                    .exerciseComment(exercise.getExerciseComment())
                    .activation_muscle_id(muscleIds)
                    .build();

            exerciseDtos.add(exerciseDto);
        }

        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .memberId(report.getMember().getMemberId())
                .compositionLogId(report.getCompositionLog().getCompositionLogId())
                .trainerName(report.getTrainer().getTrainerName())
                .reportComment(report.getReportComment())
                .createdAt(report.getCreatedAt().toString())
                .reportExercises(exerciseDtos)
                .build();
    }
}