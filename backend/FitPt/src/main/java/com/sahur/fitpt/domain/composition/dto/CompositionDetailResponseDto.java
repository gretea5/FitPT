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
    private String weightClassify;
    private List<String> weightColumn;
    private String bfmClassify;
    private List<String> bfmColumn;
    private String bfpClassify;
    private List<String> bfpColumn;
    private String smmClassify;
    private List<String> smmColumn;
    private String bmiClassify;
    private List<String> bmiColumn;
    private String tcwClassify;
    private List<String> tcwColumn;
    private String proteinClassify;
    private List<String> proteinColumn;
    private String mineralClassify;
    private List<String> mineralColumn;
    private String ecwRatioClassify;
    private List<String> ecwRatioColumn;
}
