package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.reportExercises WHERE r.member.memberId = :memberId")
    List<Report> findAllByMemberIdWithExercises(@Param("memberId") Long memberId);

    Optional<Report> findByReportId(Long reportId);
}