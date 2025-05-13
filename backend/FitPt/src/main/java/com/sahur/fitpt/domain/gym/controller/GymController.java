package com.sahur.fitpt.domain.gym.controller;

import com.sahur.fitpt.domain.gym.dto.GymResponseDto;
import com.sahur.fitpt.domain.gym.service.GymService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
@Tag(name = "Gym", description = "체육관 관리 API")
@Validated
public class GymController {
    private final GymService gymService;

    @GetMapping
    @Operation(summary = "체육관 검색", description = "키워드로 체육관을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<List<GymResponseDto>> searchGyms(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {
        return ResponseEntity.ok(gymService.searchGymsByKeyword(keyword));
    }
}