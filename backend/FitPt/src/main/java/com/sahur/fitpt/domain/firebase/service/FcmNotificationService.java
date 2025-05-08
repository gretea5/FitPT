package com.sahur.fitpt.domain.firebase.service;

import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.db.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseCloudMessageService fcmService;

    @Async
    public void sendNotificationToUser(Long memberId, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findByMember_MemberId(memberId);

        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for memberId: {}", memberId);
            return;
        }

        for (FcmToken token : tokens) {
            try {
                fcmService.sendMessageTo(token.getToken(), title, body);
            } catch (IOException e) {
                log.error("Failed to send FCM to token: {}", token.getToken(), e);
            }
        }
    }
}
