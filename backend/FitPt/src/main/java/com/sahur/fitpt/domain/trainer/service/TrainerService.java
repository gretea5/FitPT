package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerAuthResponseDto;

public interface TrainerService {
    // 기존 메서드
    // Long trainerLogin(String trainerLoginId, String trainerPassword);
    // Long trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto);
    // void trainerLogout(Long trainerId);

    // 수정 메서드
    TrainerAuthResponseDto trainerLogin(String trainerLoginId, String trainerPassword);
    TrainerAuthResponseDto trainerSignUp(TrainerSignUpRequestDto trainerSignUpRequestDto);
    void trainerLogout(Long trainerId);

    // 추가: 토큰 재발급 메서드
    String reissueAccessToken(Long trainerId, String refreshToken);
}