package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;

import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;

    @Override
    public Long trainerLogin(String trainerLoginId, String trainerPassword) {
        if (trainerLoginId == null || trainerPassword == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Trainer trainer = trainerRepository.findByTrainerLoginId(trainerLoginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        String encodedPassword = Base64Util.encode(trainerPassword);

        if (encodedPassword.equals(trainerPassword)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        return trainer.getTrainerId();
    }

    @Override
    public Long trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto) {
        if (trainerSignUpRequestDto.getTrainerLoginId() == null || trainerSignUpRequestDto.getTrainerLoginId().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (trainerSignUpRequestDto.getTrainerPw() == null || trainerSignUpRequestDto.getTrainerPw().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }


        if (trainerRepository.existsByTrainerLoginId(trainerSignUpRequestDto.getTrainerLoginId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = Base64Util.encode(trainerSignUpRequestDto.getTrainerPw());

        Admin admin = adminRepository.findById(trainerSignUpRequestDto.getAdminId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

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
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (!trainerRepository.existsByTrainerId(trainerId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }
}
