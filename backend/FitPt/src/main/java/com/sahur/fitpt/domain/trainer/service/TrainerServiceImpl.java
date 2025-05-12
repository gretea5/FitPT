package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;

import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;

    @Override
    public Long trainerLogin(String trainerLoginId, String trainerPassword) {
        if (trainerLoginId == null || trainerPassword == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        Trainer trainer = trainerRepository.findByTrainerLoginId(trainerLoginId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRAINER_NOT_FOUND));

        String encodedPassword = Base64Util.encode(trainerPassword);

        if (!encodedPassword.equals(trainer.getTrainerPw())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return trainer.getTrainerId();
    }

    @Override
    public Long trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto) {
        if (trainerSignUpRequestDto.getTrainerLoginId() == null || trainerSignUpRequestDto.getTrainerLoginId().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (trainerSignUpRequestDto.getTrainerPw() == null || trainerSignUpRequestDto.getTrainerPw().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }


        if (trainerRepository.existsByTrainerLoginId(trainerSignUpRequestDto.getTrainerLoginId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        String encodedPassword = Base64Util.encode(trainerSignUpRequestDto.getTrainerPw());

        Admin admin = adminRepository.findById(trainerSignUpRequestDto.getAdminId())
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        Trainer trainer = Trainer.builder()
                .admin(admin)
                .trainerName(trainerSignUpRequestDto.getTrainerName())
                .trainerLoginId(trainerSignUpRequestDto.getTrainerLoginId())
                .trainerPw(encodedPassword)
                .build();

        Trainer savedTrainer = trainerRepository.save(trainer);

        return savedTrainer.getTrainerId();
    }

    @Override
    public void trainerLogout(Long trainerId) {
        if (trainerId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (!trainerRepository.existsByTrainerId(trainerId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
