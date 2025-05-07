package com.sahur.fitpt.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPartialUpdateDto {
    private String memberName;
    private Float memberWeight;
}