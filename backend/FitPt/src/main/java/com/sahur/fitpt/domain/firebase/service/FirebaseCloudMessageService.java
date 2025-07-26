package com.sahur.fitpt.domain.firebase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.domain.firebase.dto.FcmMessage;
import com.sahur.fitpt.domain.firebase.dto.FcmMessageWithData;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FirebaseCloudMessageService {

    @Value("${firebase.config-path}")
    private String firebaseConfigPath;

    @Value("${fcm.api-url}")
    private String fcmApiUrl;


    public final ObjectMapper objectMapper;

    public FirebaseCloudMessageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getAccessToken() throws IOException {
        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는객체
        GoogleCredentials googleCredentials = GoogleCredentials
                // 서버로부터 받은 service key 파일 활용
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                // 인증하는 서버에서 필요로 하는 권한 지정
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        String token = googleCredentials.getAccessToken().getTokenValue();

        log.info("Access token: {}", token);

        return token;
    }

    /**
     * FCM 알림 메시지 생성
     *
     * @param targetToken
     * @param title
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage.Notification noti = new FcmMessage.Notification(title, body, null);
        FcmMessage.Message message = new FcmMessage.Message(noti, targetToken);
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     *
     * @param targetToken
     * @param title
     * @param body
     * @throws IOException
     */
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(fcmApiUrl)
                .post(requestBody)
                // 전송 토큰 추가
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        int statusCode = response.code();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            log.info("✅ FCM 메시지 전송 성공 (status: {})", statusCode);
            log.info("response body: {}", responseBody);
        } else {
            log.error("❌ FCM 메시지 전송 실패 (status: {})", statusCode);
            log.error("response body: {}", responseBody);
        }

    }

    /**
     * FCM 알림 메시지 생성
     * background 대응을 위해서 data로 전송한다.
     *
     * @param targetToken
     * @param title
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private String makeDataMessage(String targetToken, String title, String body, Long reportId) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("reportId", String.valueOf(reportId));

        FcmMessageWithData.Message message = new FcmMessageWithData.Message();
        FcmMessageWithData.Notification noti = new FcmMessageWithData.Notification(title, body, null);
        message.setToken(targetToken);
        message.setData(map);
        message.setNotification(noti);

        FcmMessageWithData fcmMessage = new FcmMessageWithData(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * background 대응을 위해서 data로 전송한다.
     *
     * @param targetToken
     * @param title
     * @param body
     * @param reportId
     * @throws IOException
     */
    public void sendDataMessageTo(String targetToken, String title, String body, Long reportId) throws IOException {
        String message = makeDataMessage(targetToken, title, body, reportId);

        log.info("FCM 전송 요청: {}", message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(fcmApiUrl)
                .post(requestBody)
                // 전송 토큰 추가
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "null";

            if (response.isSuccessful()) {
                log.info("✅ FCM 전송 성공: {}", responseBody);
            } else {
                log.error("❌ FCM 전송 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
            }
        }
    }

}
