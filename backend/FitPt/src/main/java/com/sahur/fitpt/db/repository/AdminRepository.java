package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
