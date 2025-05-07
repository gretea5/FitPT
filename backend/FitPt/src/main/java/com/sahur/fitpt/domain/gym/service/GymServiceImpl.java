package com.sahur.fitpt.domain.gym.service;

import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.domain.gym.dto.GymResponseDto;
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

    @Override
    @Transactional(readOnly = true)
    public List<GymResponseDto> searchGymsByKeyword(String keyword) {
        if (keyword == null || keyword.length() < 2) {
            throw new IllegalArgumentException("검색어는 2글자 이상이어야 합니다");
        }

        var gyms = adminRepository.findByGymNameContainingAndKeywordLengthGreaterThanEqual(keyword);
        log.debug("키워드 '{}' 검색 결과: {}개의 체육관이 검색되었습니다", keyword, gyms.size());

        return gyms.stream()
                .map(GymResponseDto::from)
                .collect(Collectors.toList());
    }
}