package com.sahur.fitpt.domain.trainer.service;

import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.constant.Role;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;
import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerAuthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TrainerAuthResponseDto trainerLogin(String trainerLoginId, String trainerPassword) {
        if (trainerLoginId == null || trainerPassword == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        Trainer trainer = trainerRepository.findByTrainerLoginId(trainerLoginId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRAINER_NOT_FOUND));

        if (!passwordEncoder.matches(trainerPassword, trainer.getTrainerPw())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtUtil.createAccessToken(trainer.getTrainerId(), Role.TRAINER.getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        redisTemplate.opsForValue().set(
                "RT:TRAINER:" + trainer.getTrainerId(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );

        return TrainerAuthResponseDto.builder()
                .trainerId(trainer.getTrainerId())
                .trainerName(trainer.getTrainerName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public TrainerAuthResponseDto trainerSignUp(TrainerSignUpRequestDto dto) {
        if (dto.getTrainerLoginId() == null || dto.getTrainerLoginId().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (dto.getTrainerPw() == null || dto.getTrainerPw().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (trainerRepository.existsByTrainerLoginId(dto.getTrainerLoginId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        //String encodedPassword = Base64Util.encode(dto.getTrainerPw());
        String encodedPassword = passwordEncoder.encode(dto.getTrainerPw());


//        Admin admin = adminRepository.findById(dto.getAdminId())
//                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        Trainer trainer = Trainer.builder()
//                .admin(admin)
                .trainerName(dto.getTrainerName())
                .trainerLoginId(dto.getTrainerLoginId())
                .trainerPw(encodedPassword)
                .build();

        Trainer savedTrainer = trainerRepository.save(trainer);

        String accessToken = jwtUtil.createAccessToken(savedTrainer.getTrainerId(), Role.TRAINER.getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        redisTemplate.opsForValue().set(
                "RT:TRAINER:" + savedTrainer.getTrainerId(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );

        return TrainerAuthResponseDto.builder()
                .trainerId(savedTrainer.getTrainerId())
                .trainerName(savedTrainer.getTrainerName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void trainerLogout(Long trainerId) {
        if (trainerId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (!trainerRepository.existsByTrainerId(trainerId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        redisTemplate.delete("RT:TRAINER:" + trainerId);
    }

    @Override
    public String reissueAccessToken(Long trainerId, String refreshToken) {
        // Redis에서 저장된 Refresh 토큰 조회
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:TRAINER:" + trainerId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 트레이너 존재 확인
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRAINER_NOT_FOUND));

        // 새로운 액세스 토큰 발급
        return jwtUtil.createAccessToken(trainerId, Role.TRAINER.getKey());
    }
}