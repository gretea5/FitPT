package com.sahur.fitpt.domain.composition.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionDetailResponseDto {
    private Long compositionLogId;
    private Long memberId;
    private LocalDateTime createdAt; // 그대로 사용
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

    private String weightLabel;
    private Integer weightCount;

    private String bfmLabel;
    private Integer bfmCount;

    private String bfpLabel;
    private Integer bfpCount;

    private String smmLabel;
    private Integer smmCount;

    private String bmiLabel;
    private Integer bmiCount;

    private String tcwLabel;
    private Integer tcwCount;

    private String proteinLabel;
    private Integer proteinCount;

    private String mineralLabel;
    private Integer mineralCount;

    private String ecwRatioLabel;
    private Integer ecwRatioCount;

}
