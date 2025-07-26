package com.sahur.fitpt.domain.firebase.service;

import com.sahur.fitpt.domain.firebase.dto.FcmRequestDto;

public interface FcmService {
    Long registerFcmToken(FcmRequestDto fcmRequestDto);
}
