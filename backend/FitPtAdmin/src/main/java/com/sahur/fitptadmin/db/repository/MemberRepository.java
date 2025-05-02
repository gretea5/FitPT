package com.sahur.fitptadmin.db.repository;

import com.sahur.fitptadmin.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m JOIN FETCH m.trainer WHERE m.admin.adminId = :adminId")
    List<Member> findAllWithTrainerByAdminId(@Param("adminId") Long adminId);
}
