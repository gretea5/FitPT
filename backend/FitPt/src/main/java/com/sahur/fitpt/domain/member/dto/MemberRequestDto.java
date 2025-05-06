package com.sahur.fitpt.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    private Long memberId;
    private Long trainerId;
    private Long adminId;
    private String memberName;
    private String memberGender;
    private LocalDateTime memberBirth;
    private Float memberHeight;
    private Float memberWeight;
}