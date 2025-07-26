package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.startTime >= :dayStart AND s.startTime < :dayEnd " +
            "AND s.trainer.trainerId = :trainerId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByDateAndTrainer(
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd,
            @Param("trainerId") Long trainerId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.startTime >= :dayStart AND s.startTime < :dayEnd " +
            "AND s.member.memberId = :memberId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByDateAndMember(
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd,
            @Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.startTime >= :monthStart AND s.startTime < :monthEnd " +
            "AND s.member.memberId = :memberId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByMonthAndMember(
            @Param("monthStart") LocalDateTime monthStart,
            @Param("monthEnd") LocalDateTime monthEnd,
            @Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.startTime >= :monthStart AND s.startTime < :monthEnd " +
            "AND s.trainer.trainerId = :trainerId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByMonthAndTrainer(
            @Param("monthStart") LocalDateTime monthStart,
            @Param("monthEnd") LocalDateTime monthEnd,
            @Param("trainerId") Long trainerId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s WHERE s.trainer.trainerId = :trainerId " +
            "AND NOT (s.endTime <= :startTime OR s.startTime >= :endTime)")
    List<Schedule> findOverlappingSchedulesForTrainer(
            @Param("trainerId") Long trainerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s WHERE s.member.memberId = :memberId " +
            "AND NOT (s.endTime <= :startTime OR s.startTime >= :endTime)")
    List<Schedule> findOverlappingSchedulesForMember(
            @Param("memberId") Long memberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


    boolean existsByScheduleIdAndTrainerTrainerId(Long scheduleId, Long authenticatedId);
}