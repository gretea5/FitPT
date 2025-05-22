package com.sahur.fitpt.domain.trainer.controller;

import com.sahur.fitpt.domain.trainer.dto.TrainerLoginRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerLogoutResponseDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerSignUpRequestDto;
import com.sahur.fitpt.domain.trainer.dto.TrainerAuthResponseDto;
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
    @Operation(summary = "트레이너 로그인", description = "트레이너 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<TrainerAuthResponseDto> login(@RequestBody TrainerLoginRequestDto request) {
        // 기존 로직
        // return new ResponseEntity<>(trainerService.trainerLogin(request.getTrainerLoginId(), request.getTrainerPw()), HttpStatus.OK);

        // 수정된 로직
        return new ResponseEntity<>(trainerService.trainerLogin(request.getTrainerLoginId(), request.getTrainerPw()), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "트레이너 회원가입", description = "트레이너 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<TrainerAuthResponseDto> signup(@RequestBody TrainerSignUpRequestDto request) {
        // 기존 로직
        // return new ResponseEntity<>(trainerService.trainerSignUp(request), HttpStatus.OK);

        // 수정된 로직
        return new ResponseEntity<>(trainerService.trainerSignUp(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "트레이너 로그아웃", description = "트레이너 로그아웃 API")
    public ResponseEntity<TrainerLogoutResponseDto> logout(
            @Parameter(hidden = true) @RequestAttribute("trainerId") Long trainerId) {
        // 기존 로직
        // trainerService.trainerLogout(trainerId);
        // return new ResponseEntity<>(new TrainerLogoutResponseDto(), HttpStatus.OK);

        // 수정된 로직
        trainerService.trainerLogout(trainerId);
        return new ResponseEntity<>(new TrainerLogoutResponseDto(), HttpStatus.OK);
    }

    // 새로 추가: 토큰 재발급 API
    @PostMapping("/reissue")
    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    public ResponseEntity<String> reissueAccessToken(
            @Parameter(hidden = true) @RequestAttribute("trainerId") Long trainerId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken
    ) {
        String refreshToken = bearerToken.substring(7);
        String newAccessToken = trainerService.reissueAccessToken(trainerId, refreshToken);
        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }
}