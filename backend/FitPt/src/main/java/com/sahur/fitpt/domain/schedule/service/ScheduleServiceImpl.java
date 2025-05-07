package com.sahur.fitpt.domain.schedule.service;

import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.Schedule;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.ScheduleRepository;
import com.sahur.fitpt.domain.schedule.dto.ScheduleRequestDto;
import com.sahur.fitpt.domain.schedule.dto.ScheduleResponseDto;
import com.sahur.fitpt.domain.schedule.validator.ScheduleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleValidator scheduleValidator;

    @Override
    @Transactional
    public Long createSchedule(ScheduleRequestDto requestDto) {
        Member member = scheduleValidator.validateAndGetMember(requestDto.getMemberId());
        Trainer trainer = scheduleValidator.validateAndGetTrainer(requestDto.getTrainerId());

        LocalDateTime startTime = requestDto.getStartTime();
        LocalDateTime endTime = requestDto.getEndTime();

        // 시간 유효성 검사
        scheduleValidator.validateScheduleTime(startTime, endTime);

        // 시간 중복 검사
        List<Schedule> trainerSchedules = scheduleRepository.findOverlappingSchedulesForTrainer(
                trainer.getTrainerId(), startTime, endTime);
        scheduleValidator.validateScheduleTimeOverlap(trainerSchedules, "Trainer");

        List<Schedule> memberSchedules = scheduleRepository.findOverlappingSchedulesForMember(
                member.getMemberId(), startTime, endTime);
        scheduleValidator.validateScheduleTimeOverlap(memberSchedules, "Member");

        Schedule schedule = Schedule.builder()
                .member(member)
                .trainer(trainer)
                .startTime(startTime)
                .endTime(endTime)
                .scheduleContent(requestDto.getScheduleContent())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Created new schedule: id={}, trainer={}, member={}, startTime={}",
                savedSchedule.getScheduleId(),
                trainer.getTrainerId(),
                member.getMemberId(),
                startTime);

        return savedSchedule.getScheduleId();
    }

    @Override
    @Transactional
    public Long updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        Member member = scheduleValidator.validateAndGetMember(requestDto.getMemberId());
        Trainer trainer = scheduleValidator.validateAndGetTrainer(requestDto.getTrainerId());

        LocalDateTime startTime = requestDto.getStartTime();
        LocalDateTime endTime = requestDto.getEndTime();

        // 시간 유효성 검사
        scheduleValidator.validateScheduleTime(startTime, endTime);

        // 시간 중복 검사 (자기 자신 제외)
        List<Schedule> trainerSchedules = scheduleRepository.findOverlappingSchedulesForTrainer(
                        trainer.getTrainerId(), startTime, endTime).stream()
                .filter(s -> !s.getScheduleId().equals(scheduleId))
                .collect(Collectors.toList());
        scheduleValidator.validateScheduleTimeOverlap(trainerSchedules, "Trainer");

        List<Schedule> memberSchedules = scheduleRepository.findOverlappingSchedulesForMember(
                        member.getMemberId(), startTime, endTime).stream()
                .filter(s -> !s.getScheduleId().equals(scheduleId))
                .collect(Collectors.toList());
        scheduleValidator.validateScheduleTimeOverlap(memberSchedules, "Member");

        schedule.update(member, trainer, startTime, endTime, requestDto.getScheduleContent());

        log.info("Updated schedule: id={}, trainer={}, member={}, startTime={}",
                schedule.getScheduleId(),
                trainer.getTrainerId(),
                member.getMemberId(),
                startTime);

        return schedule.getScheduleId();
    }

    @Override
    @Transactional
    public Long deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        scheduleRepository.delete(schedule);

        log.info("Deleted schedule: id={}", scheduleId);

        return scheduleId;
    }



    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByDateAndTrainer(int year, int month, int day, Long trainerId) {
        scheduleValidator.validateTrainerExists(trainerId);

        log.debug("Searching schedules for trainer {} on {}-{}-{}", trainerId, year, month, day);
        List<Schedule> schedules = scheduleRepository.findAllByDateAndTrainer(year, month, day, trainerId);
        log.debug("Found {} schedules in database", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByDateAndMember(int year, int month, int day, Long memberId) {
        scheduleValidator.validateMemberExists(memberId);

        log.debug("Searching schedules for member {} on {}-{}-{}", memberId, year, month, day);
        List<Schedule> schedules = scheduleRepository.findAllByDateAndMember(year, month, day, memberId);
        log.debug("Found {} schedules in database", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByMonthAndMember(int year, int month, Long memberId) {
        scheduleValidator.validateMemberExists(memberId);

        log.debug("Searching schedules for member {} in {}-{}", memberId, year, month);
        List<Schedule> schedules = scheduleRepository.findAllByMonthAndMember(year, month, memberId);
        log.debug("Found {} schedules in database", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByMonthAndTrainer(int year, int month, Long trainerId) {
        scheduleValidator.validateTrainerExists(trainerId);

        log.debug("Searching schedules for trainer {} in {}-{}", trainerId, year, month);
        List<Schedule> schedules = scheduleRepository.findAllByMonthAndTrainer(year, month, trainerId);
        log.debug("Found {} schedules in database", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }
}