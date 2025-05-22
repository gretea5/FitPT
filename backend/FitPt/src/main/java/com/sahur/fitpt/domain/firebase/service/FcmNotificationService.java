package com.sahur.fitpt.domain.firebase.service;

import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.db.entity.Notification;
import com.sahur.fitpt.db.entity.Report;
import com.sahur.fitpt.db.repository.FcmTokenRepository;
import com.sahur.fitpt.db.repository.NotificationRepository;
import com.sahur.fitpt.db.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final FirebaseCloudMessageService fcmService;
    private final ReportRepository reportRepository;

    @Async
    public void sendNotificationToUser(Long memberId, Long reportId, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findByMember_MemberId(memberId);
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for memberId: {}", memberId);
            return;
        }

        for (FcmToken token : tokens) {
            try {
                fcmService.sendDataMessageTo(token.getToken(), title, body, reportId);
            } catch (IOException e) {
                log.error("Failed to send FCM to token: {}", token.getToken(), e);
            }
        }

        Notification notification = Notification.builder()
                .member(tokens.get(0).getMember())
                .report(report)
                .notificationMessage(body)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

    }
}
