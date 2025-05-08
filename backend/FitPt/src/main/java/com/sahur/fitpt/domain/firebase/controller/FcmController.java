package com.sahur.fitpt.domain.firebase.controller;

import com.sahur.fitpt.domain.firebase.dto.FcmRequestDto;
import com.sahur.fitpt.domain.firebase.service.FcmService;
import com.sahur.fitpt.domain.firebase.service.FirebaseCloudMessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/firebase")
@RequiredArgsConstructor
@Slf4j
public class FcmController {
    private final FcmService firebaseService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("")
    @Operation(summary = "Fcm 토큰 전송")
    public ResponseEntity<Long> saveFcmToken(@RequestBody FcmRequestDto fcmRequestDto) {
        return ResponseEntity.ok(firebaseService.registerFcmToken(fcmRequestDto));
    }

    @PostMapping("/sendMessageTo")
    public void sendMessageTo(String token, String title, String body) throws IOException {
        log.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
        firebaseCloudMessageService.sendMessageTo(token, title, body);
    }
}
