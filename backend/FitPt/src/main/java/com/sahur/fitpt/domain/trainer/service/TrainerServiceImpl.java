package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;

import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;

    @Override
    public Long trainerLogin(String trainerLoginId, String trainerPassword) {
        Trainer trainer = trainerRepository.findByTrainerLoginId(trainerLoginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        if (!trainer.getTrainerPw().equals(trainerPassword)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        return trainer.getTrainerId();
    }

    @Override
    public Long trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto) {
        if (trainerRepository.existsByTrainerLoginId(trainerSignUpRequestDto.getTrainerLoginId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Admin admin = adminRepository.findById(trainerSignUpRequestDto.getAdminId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

        Trainer trainer = Trainer.builder()
                .admin(admin)
                .trainerName(trainerSignUpRequestDto.getTrainerName())
                .trainerLoginId(trainerSignUpRequestDto.getTrainerLoginId())
                .trainerPw(trainerSignUpRequestDto.getTrainerPw())
                .build();

        Trainer savedTrainer = trainerRepository.save(trainer);

        return savedTrainer.getTrainerId();
    }

    @Override
    public void trainerLogout(Long trainerId) {
        boolean loginIdExists = trainerRepository.existsByTrainerId(trainerId);

        if (!loginIdExists) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }
}
