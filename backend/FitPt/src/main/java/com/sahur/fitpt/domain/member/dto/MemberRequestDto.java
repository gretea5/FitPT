package com.sahur.fitpt.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    private String memberName;
    private String memberGender;
    private LocalDate memberBirth;
    private Float memberHeight;
    private Float memberWeight;
    private Long adminId;
    private String gymName;
}