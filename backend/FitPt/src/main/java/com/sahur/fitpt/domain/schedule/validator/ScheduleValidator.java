package com.sahur.fitpt.domain.schedule.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.sahur.fitpt.db.entity.Schedule;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleValidator {
    private static final LocalTime BUSINESS_START_TIME = LocalTime.of(6, 0);
    private static final LocalTime BUSINESS_END_TIME = LocalTime.of(22, 0);
    private static final Duration MIN_PT_DURATION = Duration.ofMinutes(30);
    private static final Duration MAX_PT_DURATION = Duration.ofHours(2);
    private static final int MAX_DAYS_IN_ADVANCE = 30;

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    public Member validateAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
    }

    public Trainer validateAndGetTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainerId));
    }

    public void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
    }

    public void validateTrainerExists(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new IllegalArgumentException("Trainer not found with ID: " + trainerId);
        }
    }


    public void validateScheduleTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time must not be null");
        }

        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule in the past");
        }

        LocalTime startTimeOfDay = startTime.toLocalTime();
        LocalTime endTimeOfDay = endTime.toLocalTime();

        if (startTimeOfDay.isBefore(BUSINESS_START_TIME) || endTimeOfDay.isAfter(BUSINESS_END_TIME)) {
            throw new IllegalArgumentException(
                    String.format("Schedule must be between %s and %s",
                            BUSINESS_START_TIME,
                            BUSINESS_END_TIME)
            );
        }

        validateScheduleDuration(startTime, endTime);
        validateScheduleDate(startTime);
    }

    private void validateScheduleDuration(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);

        if (duration.compareTo(MIN_PT_DURATION) < 0) {
            throw new IllegalArgumentException(
                    String.format("PT session must be at least %d minutes", MIN_PT_DURATION.toMinutes())
            );
        }

        if (duration.compareTo(MAX_PT_DURATION) > 0) {
            throw new IllegalArgumentException(
                    String.format("PT session cannot exceed %d hours", MAX_PT_DURATION.toHours())
            );
        }
    }

    private void validateScheduleDate(LocalDateTime startTime) {
        LocalDateTime maxFutureDate = LocalDateTime.now().plusDays(MAX_DAYS_IN_ADVANCE);

        if (startTime.isAfter(maxFutureDate)) {
            throw new IllegalArgumentException(
                    String.format("Cannot schedule more than %d days in advance", MAX_DAYS_IN_ADVANCE)
            );
        }
    }

    public void validateScheduleTimeOverlap(List<Schedule> overlappingSchedules, String userType) {
        if (!overlappingSchedules.isEmpty()) {
            log.warn("{} already has {} overlapping schedules", userType, overlappingSchedules.size());
            throw new IllegalArgumentException(
                    String.format("The %s already has a schedule during this time", userType.toLowerCase())
            );
        }
    }
}