package com.sahur.fitpt.domain.composition.dto;

import com.sahur.fitpt.db.entity.CompositionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompositionResponseDto {
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

    public static CompositionResponseDto fromEntity(CompositionLog entity) {
        CompositionResponseDto dto = new CompositionResponseDto();
        dto.compositionLogId = entity.getCompositionLogId();
        dto.memberId = entity.getMember().getMemberId();
        dto.createdAt = entity.getCreatedAt();
        dto.protein = entity.getProtein();
        dto.bmr = entity.getBmr();
        dto.mineral = entity.getMineral();
        dto.bodyAge = entity.getBodyAge();
        dto.smm = entity.getSmm();
        dto.icw = entity.getIcw();
        dto.ecw = entity.getEcw();
        dto.bfm = entity.getBfm();
        dto.bfp = entity.getBfp();
        dto.weight = entity.getWeight();
        return dto;
    }
}
