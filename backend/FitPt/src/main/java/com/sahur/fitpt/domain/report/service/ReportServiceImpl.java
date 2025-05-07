package com.sahur.fitpt.domain.report.service;

import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.*;
import com.sahur.fitpt.db.repository.*;

import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import com.sahur.fitpt.domain.report.dto.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportExerciseRepository reportExerciseRepository;
    private final WorkOutMuscleRepository workOutMuscleRepository;

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final CompositionRepository compositionRepository;

    @Override
    public Long createReport(ReportRequestDto requestDto) {
        if (requestDto.getMemberId() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (requestDto.getTrainerId() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (requestDto.getCompositionLogId() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        Trainer trainer = trainerRepository.findById(requestDto.getTrainerId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        Report report = Report.builder()
                .member(member)
                .trainer(trainer)
                .compositionLog(compositionLog)
                .reportComment(requestDto.getReportComment())
                .reportExercises(new ArrayList<>())
                .build();

        Report savedReport = reportRepository.save(report);

        List<ReportExercise> reportExercises = new ArrayList<>();

        for (ReportExerciseRequestDto exerciseDto : requestDto.getReportExercises()) {
            ReportExercise reportExercise = ReportExercise.builder()
                    .report(savedReport)
                    .exerciseName(exerciseDto.getExerciseName())
                    .exerciseAchievement(String.valueOf(exerciseDto.getExerciseAchievement()))
                    .exerciseComment(exerciseDto.getExerciseComment())
                    .workoutMuscles(new ArrayList<>())
                    .build();

            reportExercises.add(reportExercise);
        }

        List<ReportExercise> savedReportExercises = reportExerciseRepository.saveAll(reportExercises);

        List<WorkoutMuscle> allWorkoutMuscles = new ArrayList<>();

        for (int i = 0; i < savedReportExercises.size(); i++) {
            ReportExercise savedExercise = savedReportExercises.get(i);
            ReportExerciseRequestDto exerciseDto = requestDto.getReportExercises().get(i);

            List<WorkoutMuscle> workoutMuscles = new ArrayList<>();
            for (Long muscleId : exerciseDto.getActivation_muscle_id()) {
                WorkoutMuscle workoutMuscle = WorkoutMuscle.builder()
                        .reportExercise(savedExercise)
                        .activationMuscleId(muscleId)
                        .build();
                workoutMuscles.add(workoutMuscle);
            }

            allWorkoutMuscles.addAll(workoutMuscles);
        }

        workOutMuscleRepository.saveAll(allWorkoutMuscles);

        return savedReport.getReportId();
    }

    @Override
    public Long updateReport(Long reportId, ReportRequestDto requestDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        if (requestDto.getMemberId() != null) {
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));
            report.updateMember(member);
        }

        if (requestDto.getCompositionLogId() != null) {
            CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));
            report.updateCompositionLog(compositionLog);
        }

        if (requestDto.getReportComment() != null) {
            report.updateReportComment(requestDto.getReportComment());
        }

        if (requestDto.getReportExercises() != null && !requestDto.getReportExercises().isEmpty()) {
            List<ReportExercise> existingExercises = reportExerciseRepository.findAllByReport(report);

            for (ReportExercise exercise : existingExercises) {
                workOutMuscleRepository.deleteAllByReportExercise(exercise);
            }

            reportExerciseRepository.deleteAllByReport(report);

            for (ReportExerciseRequestDto exerciseDto : requestDto.getReportExercises()) {
                ReportExercise reportExercise = ReportExercise.builder()
                        .report(report)
                        .exerciseName(exerciseDto.getExerciseName())
                        .exerciseAchievement(String.valueOf(exerciseDto.getExerciseAchievement()))
                        .exerciseComment(exerciseDto.getExerciseComment())
                        .workoutMuscles(new ArrayList<>())
                        .build();

                ReportExercise savedExercise = reportExerciseRepository.save(reportExercise);

                List<WorkoutMuscle> workoutMuscles = new ArrayList<>();
                for (Long muscleId : exerciseDto.getActivation_muscle_id()) {
                    WorkoutMuscle workoutMuscle = WorkoutMuscle.builder()
                            .reportExercise(savedExercise)
                            .activationMuscleId(muscleId)
                            .build();
                    workoutMuscles.add(workoutMuscle);
                }

                workOutMuscleRepository.saveAll(workoutMuscles);
            }
        }

        return reportId;
    }

    @Override
    public List<ReportResponseDto> getAllReports(Long memberId) {
        List<Report> reports = reportRepository.findAllByMemberIdWithExercises(memberId);

        List<ReportExercise> exercises = reportExerciseRepository.findAllWithWorkoutMusclesByReportIn(reports);

        return reports.stream()
                .map(ReportResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDetailResponseDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND)
        );

        CompositionLog compositionLog = compositionRepository.findById(report.getCompositionLog().getCompositionLogId())
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        CompositionResponseDto compositionResponseDto = CompositionResponseDto.fromEntity(compositionLog);

        List<ReportExercise> reportExercises = reportExerciseRepository.findAllWithWorkoutMusclesByReport(report);

        ReportDetailResponseDto dto = new ReportDetailResponseDto();

        dto.setReportId(report.getReportId());
        dto.setMemberId(report.getMember().getMemberId());
        dto.setTrainerName(report.getTrainer().getTrainerName());
        dto.setReportComment(report.getReportComment());
        dto.setCreatedAt(report.getCreatedAt().toString());
        dto.setCompositionResponseDto(compositionResponseDto);
        dto.setReportExercises(new ArrayList<>());

        for (ReportExercise exercise : reportExercises) {
            ReportExerciseResponseDto reportExerciseResponseDto = new ReportExerciseResponseDto();

            reportExerciseResponseDto.setExerciseComment(exercise.getExerciseComment());
            reportExerciseResponseDto.setExerciseName(exercise.getExerciseName());
            reportExerciseResponseDto.setExerciseAchievement(exercise.getExerciseAchievement());
            reportExerciseResponseDto.setActivation_muscle_id(new ArrayList<>());

            for (WorkoutMuscle workoutMuscle : exercise.getWorkoutMuscles()) {
                reportExerciseResponseDto.getActivation_muscle_id().add(workoutMuscle.getActivationMuscleId());
            }

            dto.getReportExercises().add(reportExerciseResponseDto);
        }

        return dto;
    }
}