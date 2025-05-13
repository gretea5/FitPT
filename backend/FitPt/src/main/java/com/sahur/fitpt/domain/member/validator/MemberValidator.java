package com.sahur.fitpt.domain.member.validator;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final TrainerRepository trainerRepository;

    public void validateGender(String gender) {
        if (!gender.equals("남성") && !gender.equals("여성")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public void validateTrainerExists(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new CustomException(ErrorCode.TRAINER_NOT_FOUND);
        }
    }

    public void validateMemberExists(Member member, Long memberId) {
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    public void validateHeight(Float height) {
        if (height != null && (height <= 100 || height >= 250)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public void validateWeight(Float weight) {
        if (weight != null && (weight <= 30 || weight >= 250)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }


}