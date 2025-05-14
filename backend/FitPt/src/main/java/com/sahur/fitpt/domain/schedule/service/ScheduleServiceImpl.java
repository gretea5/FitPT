package com.sahur.fitpt.domain.schedule.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
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
        log.debug("createSchedule 서비스 시작 - requestDto: {}", requestDto);

        try {
            Member member = scheduleValidator.validateAndGetMember(requestDto.getMemberId());
            log.debug("회원 검증 완료: {}", member.getMemberId());

            Trainer trainer = scheduleValidator.validateAndGetTrainer(requestDto.getTrainerId());
            log.debug("트레이너 검증 완료: {}", trainer.getTrainerId());

            // 트레이너가 해당 회원을 담당하는지 검증
            scheduleValidator.validateTrainerManagesMember(trainer.getTrainerId(), member.getMemberId());

            LocalDateTime startTime = requestDto.getStartTime();
            LocalDateTime endTime = requestDto.getEndTime();
            log.debug("시간 정보 - 시작: {}, 종료: {}", startTime, endTime);

            scheduleValidator.validateScheduleTime(startTime, endTime);
            log.debug("시간 유효성 검사 완료");

            // 시간 중복 검사
            List<Schedule> trainerSchedules = scheduleRepository.findOverlappingSchedulesForTrainer(
                    trainer.getTrainerId(), startTime, endTime);
            log.debug("트레이너 중복 일정 검사 - 중복 수: {}", trainerSchedules.size());
            scheduleValidator.validateScheduleTimeOverlap(trainerSchedules, "트레이너");

            List<Schedule> memberSchedules = scheduleRepository.findOverlappingSchedulesForMember(
                    member.getMemberId(), startTime, endTime);
            log.debug("회원 중복 일정 검사 - 중복 수: {}", memberSchedules.size());
            scheduleValidator.validateScheduleTimeOverlap(memberSchedules, "회원");

            Schedule schedule = Schedule.builder()
                    .member(member)
                    .trainer(trainer)
                    .startTime(startTime)
                    .endTime(endTime)
                    .scheduleContent(requestDto.getScheduleContent())
                    .build();

            Schedule savedSchedule = scheduleRepository.save(schedule);
            log.debug("일정 저장 완료 - ID: {}", savedSchedule.getScheduleId());

            return savedSchedule.getScheduleId();
        } catch (Exception e) {
            log.error("일정 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Long updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        Member member = scheduleValidator.validateAndGetMember(requestDto.getMemberId());
        Trainer trainer = scheduleValidator.validateAndGetTrainer(requestDto.getTrainerId());

        // 트레이너가 해당 회원을 담당하는지 검증
        scheduleValidator.validateTrainerManagesMember(trainer.getTrainerId(), member.getMemberId());

        LocalDateTime startTime = requestDto.getStartTime();
        LocalDateTime endTime = requestDto.getEndTime();

        // 시간 유효성 검사
        scheduleValidator.validateScheduleTime(startTime, endTime);

        // 시간 중복 검사 (자기 자신 제외)
        List<Schedule> trainerSchedules = scheduleRepository.findOverlappingSchedulesForTrainer(
                        trainer.getTrainerId(), startTime, endTime).stream()
                .filter(s -> !s.getScheduleId().equals(scheduleId))
                .collect(Collectors.toList());
        scheduleValidator.validateScheduleTimeOverlap(trainerSchedules, "트레이너");

        List<Schedule> memberSchedules = scheduleRepository.findOverlappingSchedulesForMember(
                        member.getMemberId(), startTime, endTime).stream()
                .filter(s -> !s.getScheduleId().equals(scheduleId))
                .collect(Collectors.toList());
        scheduleValidator.validateScheduleTimeOverlap(memberSchedules, "회원");

        schedule.update(member, trainer, startTime, endTime, requestDto.getScheduleContent());

        log.info("일정 수정 완료: ID={}, 트레이너={}, 회원={}, 시작시간={}",
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
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedule);
        log.info("일정 삭제 완료: ID={}", scheduleId);
        return scheduleId;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByDateAndMember(int year, int month, int day, Long memberId) {
        scheduleValidator.validateMemberExists(memberId);

        LocalDateTime dayStart = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime dayEnd = dayStart.plusDays(1);

        log.debug("회원 {} 일정 조회 - 날짜: {}-{}-{}", memberId, year, month, day);
        List<Schedule> schedules = scheduleRepository.findAllByDateAndMember(dayStart, dayEnd, memberId);
        log.debug("조회된 일정 수: {}", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByDateAndTrainer(int year, int month, int day, Long trainerId) {
        scheduleValidator.validateTrainerExists(trainerId);

        LocalDateTime dayStart = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime dayEnd = dayStart.plusDays(1);

        log.debug("트레이너 {} 일정 조회 - 날짜: {}-{}-{}", trainerId, year, month, day);
        List<Schedule> schedules = scheduleRepository.findAllByDateAndTrainer(dayStart, dayEnd, trainerId);
        log.debug("조회된 일정 수: {}", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByMonthAndMember(int year, int month, Long memberId) {
        scheduleValidator.validateMemberExists(memberId);

        LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        log.debug("회원 {} 일정 조회 - 년월: {}-{}", memberId, year, month);
        List<Schedule> schedules = scheduleRepository.findAllByMonthAndMember(monthStart, monthEnd, memberId);
        log.debug("조회된 일정 수: {}", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByMonthAndTrainer(int year, int month, Long trainerId) {
        scheduleValidator.validateTrainerExists(trainerId);

        LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        log.debug("트레이너 {} 일정 조회 - 년월: {}-{}", trainerId, year, month);
        List<Schedule> schedules = scheduleRepository.findAllByMonthAndTrainer(monthStart, monthEnd, trainerId);
        log.debug("조회된 일정 수: {}", schedules.size());

        return schedules.stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }
}