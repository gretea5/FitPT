package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Trainer;

import java.util.List;

public interface TrainerService {
    List<Trainer> getTrainers(Long adminId);
}
