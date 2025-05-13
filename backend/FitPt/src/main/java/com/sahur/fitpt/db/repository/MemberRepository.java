package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //  findById(논리적 삭제 고려)
    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.admin " +
            "LEFT JOIN FETCH m.trainer " +
            "WHERE m.memberId = :memberId AND m.isDeleted = false")
    Optional<Member> findByIdAndNotDeleted(@Param("memberId") Long memberId);

    // 트레이너별 회원 목록 조회 (삭제되지 않은 회원만)
    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.admin " +
            "WHERE m.trainer.trainerId = :trainerId AND m.isDeleted = false")
    List<Member> findAllByTrainerIdAndNotDeleted(@Param("trainerId") Long trainerId);

    // 카카오 로그인용 memberName으로 회원 조회 (삭제되지 않은 회원만)
    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.admin " +
            "LEFT JOIN FETCH m.trainer " +
            "WHERE m.memberName = :memberName AND m.isDeleted = false")
    Optional<Member> findByMemberName(@Param("memberName") String memberName);

    @Query("SELECT m FROM Member m " +
            "LEFT JOIN FETCH m.admin " +
            "LEFT JOIN FETCH m.trainer " +
            "WHERE m.kakaoId = :kakaoId AND m.isDeleted = false")
    Optional<Member> findByKakaoId(@Param("kakaoId") Long kakaoId);

    boolean existsByMemberIdAndTrainerTrainerId(Long memberId, Long trainerId);
}

