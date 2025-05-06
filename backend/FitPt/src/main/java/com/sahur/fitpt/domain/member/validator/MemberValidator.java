package com.sahur.fitpt.domain.member.validator;

import com.sahur.fitpt.db.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {

    public void validateGender(String gender) {
        if (!gender.equals("남성") && !gender.equals("여성")) {
            throw new IllegalArgumentException("성별은 '남성' 또는 '여성'이어야 합니다");
        }
    }

    public void validateMemberExists(Member member, Long memberId) {
        if (member == null) {
            throw new EntityNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다");
        }
    }

    public void validateHeight(Float height) {
        if (height != null && (height < 100 || height > 250)) {
            throw new IllegalArgumentException("키는 100cm에서 250cm 사이여야 합니다");
        }
    }

    public void validateWeight(Float weight) {
        if (weight != null && (weight < 30 || weight > 250)) {
            throw new IllegalArgumentException("몸무게는 30kg에서 250kg 사이여야 합니다");
        }
    }
}