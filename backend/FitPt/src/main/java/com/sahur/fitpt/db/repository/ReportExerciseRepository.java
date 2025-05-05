package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Report;
import com.sahur.fitpt.db.entity.ReportExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportExerciseRepository extends JpaRepository<ReportExercise, Integer> {
    List<ReportExercise> findAllByReport(Report report);

    void deleteAllByReport(Report report);
}