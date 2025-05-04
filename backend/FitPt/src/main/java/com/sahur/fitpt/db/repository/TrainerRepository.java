package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByTrainerLoginId(String trainerLoginId);
    boolean existsByTrainerId(Long trainerId);
    boolean existsByTrainerLoginId(String trainerLoginId);
}
