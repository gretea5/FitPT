package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}