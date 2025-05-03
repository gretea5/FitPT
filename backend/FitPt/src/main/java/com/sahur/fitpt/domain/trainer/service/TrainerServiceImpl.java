package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.TrainerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;

    @Override
    public Long trainerLogin(String trainerLoginId, String trainerPassword) {
        Trainer trainer = trainerRepository.findByTrainerLoginId(trainerLoginId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRAINER_NOT_FOUND));

        if (!trainer.getTrainerPw().equals(trainerPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return trainer.getTrainerId();
    }
}
