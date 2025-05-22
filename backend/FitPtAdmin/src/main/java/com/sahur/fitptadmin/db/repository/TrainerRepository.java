package com.sahur.fitptadmin.db.repository;

import com.sahur.fitptadmin.db.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {

    List<Trainer> findByAdmin_AdminId(Long adminId);

    boolean existsByTrainerLoginId(String trainerLoginId);
}
