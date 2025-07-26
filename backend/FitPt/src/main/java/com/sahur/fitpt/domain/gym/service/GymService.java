package com.sahur.fitpt.domain.gym.service;

import com.sahur.fitpt.domain.gym.dto.GymResponseDto;
import java.util.List;

public interface GymService {
    List<GymResponseDto> searchGymsByKeyword(String keyword);
}