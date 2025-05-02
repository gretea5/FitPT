package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
}
