package com.sahur.fitpt.domain.trainer.controller;

import com.sahur.fitpt.domain.trainer.dto.TrainerLoginRequestDto;
import com.sahur.fitpt.domain.trainer.service.TrainerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody TrainerLoginRequestDto request) {
        Long trainerId = trainerService.trainerLogin(request.getTrainerLoginId(), request.getTrainerPw());
        return ResponseEntity.ok().body(trainerId);
    }
}
