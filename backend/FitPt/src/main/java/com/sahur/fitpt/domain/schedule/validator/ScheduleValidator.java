package com.sahur.fitpt.domain.schedule.validator;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.Schedule;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleValidator {
    /** 영업 시작 시간 (오전 9시) */
    private static final LocalTime BUSINESS_START_TIME = LocalTime.of(9, 0);

    /** 영업 종료 시간 (오후 10시) */
    private static final LocalTime BUSINESS_END_TIME = LocalTime.of(22, 0);

    /** 최소 PT 세션 시간 (60분) */
    private static final Duration MIN_PT_DURATION = Duration.ofMinutes(60);

    /** 최대 PT 세션 시간 (2시간) */
    private static final Duration MAX_PT_DURATION = Duration.ofHours(2);

    /** 최대 예약 가능 미래 일수 (30일) */
    private static final int MAX_DAYS_IN_ADVANCE = 30;

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    public Member validateAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원 ID {} 를 찾을 수 없습니다", memberId);
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    public Trainer validateAndGetTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> {
                    log.error("트레이너 ID {} 를 찾을 수 없습니다", trainerId);
                    return new CustomException(ErrorCode.TRAINER_NOT_FOUND);
                });
    }

    public void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            log.error("회원 ID {} 를 찾을 수 없습니다", memberId);
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    public void validateTrainerExists(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            log.error("트레이너 ID {} 를 찾을 수 없습니다", trainerId);
            throw new CustomException(ErrorCode.TRAINER_NOT_FOUND);
        }
    }

    public void validateScheduleTime(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("validateScheduleTime 시작 - 시작: {}, 종료: {}", startTime, endTime);

        if (startTime == null || endTime == null) {
            log.error("시작 시간 또는 종료 시간이 null입니다");
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
        }

        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            log.error("시작 시간이 종료 시간보다 늦거나 같습니다: 시작={}, 종료={}", startTime, endTime);
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            log.error("과거 시간으로 일정을 잡을 수 없습니다: {}", startTime);
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
        }

        LocalTime startTimeOfDay = startTime.toLocalTime();
        LocalTime endTimeOfDay = endTime.toLocalTime();
        log.debug("영업 시간 검사 - 시작: {}, 종료: {}", startTimeOfDay, endTimeOfDay);

        if (startTimeOfDay.isBefore(BUSINESS_START_TIME) || endTimeOfDay.isAfter(BUSINESS_END_TIME)) {
            log.error("영업 시간({}~{}) 외의 시간입니다: {}~{}",
                    BUSINESS_START_TIME, BUSINESS_END_TIME, startTimeOfDay, endTimeOfDay);
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
        }

        validateScheduleDuration(startTime, endTime);
        validateScheduleDate(startTime);
        log.debug("validateScheduleTime 완료");
    }

    private void validateScheduleDuration(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);

        if (duration.compareTo(MIN_PT_DURATION) < 0) {
            log.error("PT 세션은 최소 {}분 이상이어야 합니다: {}분",
                    MIN_PT_DURATION.toMinutes(), duration.toMinutes());
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_DURATION);
        }

        if (duration.compareTo(MAX_PT_DURATION) > 0) {
            log.error("PT 세션은 최대 {}시간을 초과할 수 없습니다: {}시간",
                    MAX_PT_DURATION.toHours(), duration.toHours());
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_DURATION);
        }
    }

    private void validateScheduleDate(LocalDateTime startTime) {
        LocalDateTime maxFutureDate = LocalDateTime.now().plusDays(MAX_DAYS_IN_ADVANCE);

        if (startTime.isAfter(maxFutureDate)) {
            log.error("{}일 이상 미래의 일정은 잡을 수 없습니다: {}",
                    MAX_DAYS_IN_ADVANCE, startTime);
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_DATE);
        }
    }

    public void validateScheduleTimeOverlap(List<Schedule> overlappingSchedules, String userType) {
        if (!overlappingSchedules.isEmpty()) {
            log.error("{}에게 이미 {}개의 겹치는 일정이 있습니다",
                    userType, overlappingSchedules.size());
            throw new CustomException(ErrorCode.SCHEDULE_TIME_OVERLAP);
        }
    }

    public void validateTrainerManagesMember(Long trainerId, Long memberId) {
        boolean isManaging = memberRepository.existsByMemberIdAndTrainerTrainerId(memberId, trainerId);
        if (!isManaging) {
            log.error("트레이너 {}가 회원 {}를 담당하고 있지 않습니다", trainerId, memberId);
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }


}