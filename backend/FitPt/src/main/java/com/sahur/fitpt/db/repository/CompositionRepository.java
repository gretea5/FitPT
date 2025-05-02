package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.CompositionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompositionRepository extends JpaRepository<CompositionLog, Long> {
}
