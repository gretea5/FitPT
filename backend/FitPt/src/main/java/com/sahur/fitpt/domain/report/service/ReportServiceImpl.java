package com.sahur.fitpt.domain.report.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.*;
import com.sahur.fitpt.db.repository.*;

import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import com.sahur.fitpt.domain.firebase.service.FirebaseCloudMessageService;
import com.sahur.fitpt.domain.report.dto.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
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
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (requestDto.getTrainerId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (requestDto.getCompositionLogId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Trainer trainer = trainerRepository.findById(requestDto.getTrainerId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRAINER_NOT_FOUND));

        CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMPOSITION_NOT_FOUND));

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
            for (Long muscleId : exerciseDto.getActivationMuscleId()) {
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
        if (reportId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        if (requestDto.getMemberId() != null) {
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            report.updateMember(member);
        }

        if (requestDto.getCompositionLogId() != null) {
            CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMPOSITION_NOT_FOUND));
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
                for (Long muscleId : exerciseDto.getActivationMuscleId()) {
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
        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        List<Report> reports = reportRepository.findAllByMemberIdWithExercises(memberId);

        List<ReportExercise> exercises = reportExerciseRepository.findAllWithWorkoutMusclesByReportIn(reports);

        return reports.stream()
                .map(ReportResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDetailResponseDto getReport(Long reportId) {
        if (reportId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        Report report = reportRepository.findById(reportId).orElseThrow(() ->
                new CustomException(ErrorCode.REPORT_NOT_FOUND)
        );

        CompositionLog compositionLog = compositionRepository.findById(report.getCompositionLog().getCompositionLogId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMPOSITION_NOT_FOUND));

        CompositionResponseDto compositionResponseDto = CompositionResponseDto.fromEntity(compositionLog);

        List<ReportExercise> reportExercises = reportExerciseRepository.findAllWithWorkoutMusclesByReport(report);

        ReportDetailResponseDto dto = ReportDetailResponseDto.builder()
                .reportId(report.getReportId())
                .memberId(report.getMember().getMemberId())
                .trainerName(report.getTrainer().getTrainerName())
                .reportComment(report.getReportComment())
                .createdAt(report.getCreatedAt().toString())
                .compositionResponseDto(compositionResponseDto)
                .reportExercises(
                        reportExercises.stream()
                                .map(exercise -> ReportExerciseResponseDto.builder()
                                        .exerciseComment(exercise.getExerciseComment())
                                        .exerciseName(exercise.getExerciseName())
                                        .exerciseAchievement(exercise.getExerciseAchievement())
                                        .activationMuscleId(
                                                exercise.getWorkoutMuscles().stream()
                                                        .map(WorkoutMuscle::getActivationMuscleId)
                                                        .collect(Collectors.toList())
                                        )
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();

        return dto;
    }
}