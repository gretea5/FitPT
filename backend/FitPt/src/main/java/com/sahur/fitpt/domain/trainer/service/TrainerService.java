package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;

public interface TrainerService {
    Long trainerLogin(String trainerLoginId, String trainerPassword);
    Long trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto);
    void trainerLogout(Long trainerId);
}
