package com.sahur.fitpt.domain.gym.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.domain.gym.dto.GymResponseDto;
import com.sahur.fitpt.domain.gym.validator.GymValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymServiceImpl implements GymService {
    private final AdminRepository adminRepository;
    private final GymValidator gymValidator;

    @Override
    @Transactional(readOnly = true)
    public List<GymResponseDto> searchGymsByKeyword(String keyword) {
        gymValidator.validateSearchKeyword(keyword);

        List<Admin> gyms = adminRepository.findByGymNameContainingOrderByGymNameAsc(keyword);
        log.debug("키워드 '{}' 검색 결과: {}개의 체육관이 검색되었습니다", keyword, gyms.size());

        if (gyms.isEmpty()) {
            throw new CustomException(ErrorCode.GYM_NOT_FOUND);
        }

        return gyms.stream()
                .map(GymResponseDto::from)
                .collect(Collectors.toList());
    }
}