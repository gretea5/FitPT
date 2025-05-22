package com.sahur.fitpt.domain.composition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompositionRequestDto {
    private Long memberId;
    private Float protein;
    private Float bmr;
    private Float mineral;
    private Integer bodyAge;
    private Float smm;
    private Float icw;
    private Float ecw;
    private Float bfm;
    private Float bfp;
    private Float weight;
}
