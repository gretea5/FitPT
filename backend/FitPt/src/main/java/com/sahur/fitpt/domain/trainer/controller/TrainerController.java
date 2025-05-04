package com.sahur.fitpt.domain.trainer.controller;

import com.sahur.fitpt.domain.trainer.dto.TrainerLoginRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerLogoutResponseDto;
import com.sahur.fitpt.domain.trainer.service.TrainerService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody TrainerLoginRequestDto request) {
        Long trainerId = trainerService.trainerLogin(request.getTrainerLoginId(), request.getTrainerPw());
        return ResponseEntity.ok().body(trainerId);
    }

    @PostMapping("/{trainerId}/logout")
    @Parameters({
            @Parameter(name = "trainerId", description = "트레이너 Id", example = "1"),
    })
    public ResponseEntity<TrainerLogoutResponseDto> logout(@PathVariable("trainerId") Long trainerId) {
        trainerService.trainerLogout(trainerId);
        return ResponseEntity.ok(new TrainerLogoutResponseDto());
    }
}