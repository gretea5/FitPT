package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.db.repository.TrainerRepository;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    @Override
    public List<TrainerResponseDto> getTrainers(Long adminId) {
        List<Trainer> trainers = trainerRepository.findByAdmin_AdminId(adminId);

        return trainers.stream()
                .map(trainer -> TrainerResponseDto.builder()
                        .trainerId(trainer.getTrainerId())
                        .trainerLoginId(trainer.getTrainerLoginId())
                        .trainerName(trainer.getTrainerName())
                        .trainerBirthday(trainer.getTrainerBirthDate()) // 필드명이 DTO에 맞게 수정
                        .build())
                .toList();
    }

    @Override
    public Long updateTrainerInfo(Long trainerId, TrainerUpdateRequestDto trainerUpdateRequestDto) {

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));

        log.info(trainerUpdateRequestDto.getTrainerName());

        trainer.updateTrainerInfo(trainerUpdateRequestDto.getTrainerName(), trainerUpdateRequestDto.getTrainerBirthday());

        return trainer.getTrainerId();
    }
}
