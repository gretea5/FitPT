package com.sahur.fitptadmin.db.repository;

import com.sahur.fitptadmin.db.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    Admin findByAdminLoginId(String loginId);
}
