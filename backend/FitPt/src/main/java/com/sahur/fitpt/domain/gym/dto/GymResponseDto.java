package com.sahur.fitpt.domain.gym.dto;

import com.sahur.fitpt.db.entity.Admin;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GymResponseDto {
    private Long adminId;
    private String gymName;
    private String gymAddr;

    public static GymResponseDto from(Admin admin) {
        return GymResponseDto.builder()
                .adminId(admin.getAdminId())
                .gymName(admin.getGymName())
                .gymAddr(admin.getGymAddr())
                .build();
    }
}