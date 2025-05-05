package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Report;
import com.sahur.fitpt.db.entity.ReportExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportExerciseRepository extends JpaRepository<ReportExercise, Integer> {
    List<ReportExercise> findAllByReport(Report report);

    void deleteAllByReport(Report report);

    @Query("SELECT DISTINCT re FROM ReportExercise re LEFT JOIN FETCH re.workoutMuscles WHERE re.report IN :reports")
    List<ReportExercise> findAllWithWorkoutMusclesByReportIn(@Param("reports") List<Report> reports);

    @Query("SELECT DISTINCT re FROM ReportExercise re LEFT JOIN FETCH re.workoutMuscles WHERE re.report = :report")
    List<ReportExercise> findAllWithWorkoutMusclesByReport(@Param("report") Report report);
}