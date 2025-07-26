package com.sahur.fitptadmin.domain.member.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long memberId;
    private String memberName;
    private String memberGender;
    private LocalDate memberBirth;
    private Float memberHeight;
    private Float memberWeight;
    private String trainerName;

}
