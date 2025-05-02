package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.CompositionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CompositionRepository extends JpaRepository<CompositionLog, Long> {
    List<CompositionLog> findAllByMemberMemberId(Long memberId);
    List<CompositionLog> findAllByMemberMemberId(Long memberId, Sort sort);
}
