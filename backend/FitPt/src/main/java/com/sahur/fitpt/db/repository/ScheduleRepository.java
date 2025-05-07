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
            "WHERE FUNCTION('YEAR', s.startTime) = :year " +
            "AND FUNCTION('MONTH', s.startTime) = :month " +
            "AND FUNCTION('DAY', s.startTime) = :day " +
            "AND s.trainer.trainerId = :trainerId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByDateAndTrainer(
            @Param("year") int year,
            @Param("month") int month,
            @Param("day") int day,
            @Param("trainerId") Long trainerId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE FUNCTION('YEAR', s.startTime) = :year " +
            "AND FUNCTION('MONTH', s.startTime) = :month " +
            "AND FUNCTION('DAY', s.startTime) = :day " +
            "AND s.member.memberId = :memberId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByDateAndMember(
            @Param("year") int year,
            @Param("month") int month,
            @Param("day") int day,
            @Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE FUNCTION('YEAR', s.startTime) = :year " +
            "AND FUNCTION('MONTH', s.startTime) = :month " +
            "AND s.member.memberId = :memberId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByMonthAndMember(
            @Param("year") int year,
            @Param("month") int month,
            @Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s " +
            "WHERE FUNCTION('YEAR', s.startTime) = :year " +
            "AND FUNCTION('MONTH', s.startTime) = :month " +
            "AND s.trainer.trainerId = :trainerId " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findAllByMonthAndTrainer(
            @Param("year") int year,
            @Param("month") int month,
            @Param("trainerId") Long trainerId);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s WHERE s.trainer.trainerId = :trainerId " +
            "AND ((:startTime BETWEEN s.startTime AND s.endTime) " +
            "OR (:endTime BETWEEN s.startTime AND s.endTime) " +
            "OR (s.startTime BETWEEN :startTime AND :endTime))")
    List<Schedule> findOverlappingSchedulesForTrainer(
            @Param("trainerId") Long trainerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @EntityGraph(attributePaths = {"member", "trainer"})
    @Query("SELECT s FROM Schedule s WHERE s.member.memberId = :memberId " +
            "AND ((:startTime BETWEEN s.startTime AND s.endTime) " +
            "OR (:endTime BETWEEN s.startTime AND s.endTime) " +
            "OR (s.startTime BETWEEN :startTime AND :endTime))")
    List<Schedule> findOverlappingSchedulesForMember(
            @Param("memberId") Long memberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


}