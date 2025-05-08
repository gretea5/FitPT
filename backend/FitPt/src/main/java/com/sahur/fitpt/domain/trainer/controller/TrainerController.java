package com.sahur.fitpt.domain.trainer.controller;

import com.sahur.fitpt.domain.trainer.dto.TrainerLoginRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerLogoutResponseDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import com.sahur.fitpt.domain.trainer.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping("/login")
    @Operation(summary = "트레이너 로그인", description = "트레이너 로그인  API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Long> login(@RequestBody TrainerLoginRequestDto request) {
        return new ResponseEntity<>(trainerService.trainerLogin(request.getTrainerLoginId(), request.getTrainerPw()), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "트레이너 회원가입", description = "트레이너 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Long> signup(@RequestBody TrainerSignUpRequestDto request) {
        return new ResponseEntity<>(trainerService.trainerSignUp(request), HttpStatus.OK);
    }

    @PostMapping("/{trainerId}/logout")
    @Operation(summary = "트레이너 로그아웃", description = "트레이너 로그아웃 API")
    @Parameters({
            @Parameter(name = "trainerId", description = "트레이너 Id", example = "1"),
    })
    public ResponseEntity<TrainerLogoutResponseDto> logout(@PathVariable("trainerId") Long trainerId) {
        trainerService.trainerLogout(trainerId);
        return new ResponseEntity<>(new TrainerLogoutResponseDto(), HttpStatus.OK);
    }
}