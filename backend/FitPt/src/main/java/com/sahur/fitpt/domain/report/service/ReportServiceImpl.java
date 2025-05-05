package com.sahur.fitpt.domain.report.service;

import com.sahur.fitpt.db.entity.*;
import com.sahur.fitpt.db.repository.*;

import com.sahur.fitpt.domain.report.dto.ReportExerciseRequestDto;
import com.sahur.fitpt.domain.report.dto.ReportRequestDto;

import com.sahur.fitpt.domain.report.dto.ReportResponseDto;
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
            throw new IllegalArgumentException("회원 ID는 null이 될 수 없습니다.");
        }

        if (requestDto.getTrainerId() == null) {
            throw new IllegalArgumentException("트레이너 ID는 null이 될 수 없습니다.");
        }

        if (requestDto.getCompositionLogId() == null) {
            throw new IllegalArgumentException("체성분 로그 ID는 null이 될 수 없습니다.");
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Trainer trainer = trainerRepository.findById(requestDto.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("트레이너를 찾을 수 없습니다."));

        CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                .orElseThrow(() -> new IllegalArgumentException("체성분 로그를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("보고서를 찾을 수 없습니다."));

        if (requestDto.getMemberId() != null) {
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
            report.updateMember(member);
        }

        if (requestDto.getCompositionLogId() != null) {
            CompositionLog compositionLog = compositionRepository.findById(requestDto.getCompositionLogId())
                    .orElseThrow(() -> new IllegalArgumentException("체성분 로그를 찾을 수 없습니다."));
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
}