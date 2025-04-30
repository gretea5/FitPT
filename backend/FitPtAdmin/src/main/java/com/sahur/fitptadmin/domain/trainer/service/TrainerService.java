package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;

import java.util.List;

public interface TrainerService {
    List<TrainerResponseDto> getTrainers(Long adminId);
}
