package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

//    @Query("SELECT a FROM Admin a WHERE a.gymName LIKE %:keyword% ORDER BY a.gymName ASC")
    @Query(value = "SELECT * FROM admin " +
            "WHERE MATCH(gym_name) AGAINST(CONCAT(:keyword, '*') IN BOOLEAN MODE) " +
            "ORDER BY gym_name ASC",
            nativeQuery = true)
    List<Admin> findByGymNameContainingOrderByGymNameAsc(@Param("keyword") String keyword);
}