package com.sahur.fitpt.db.repository;


import com.sahur.fitpt.db.entity.WorkoutMuscle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOutMuscleRepository extends JpaRepository<WorkoutMuscle, Long> {
}