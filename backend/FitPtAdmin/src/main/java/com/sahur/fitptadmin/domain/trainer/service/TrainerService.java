package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerRegisterDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerUpdateRequestDto;

import java.util.List;

public interface TrainerService {
    List<TrainerResponseDto> getTrainers(Long adminId);

    Long registerTrainer(TrainerRegisterDto trainerRegisterDto);

    Long updateTrainerInfo(Long trainerId, TrainerUpdateRequestDto trainerUpdateRequestDto);
}
