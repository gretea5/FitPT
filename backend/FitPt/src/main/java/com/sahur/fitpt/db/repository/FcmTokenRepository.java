package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByTokenAndMacAddr(String token, String macAddr);

    List<FcmToken> findByMember_MemberId(Long memberId);

    void deleteAllByMember_MemberId(Long memberId);
}
