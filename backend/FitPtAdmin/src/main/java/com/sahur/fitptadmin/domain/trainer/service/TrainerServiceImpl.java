package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.db.repository.TrainerRepository;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
}
