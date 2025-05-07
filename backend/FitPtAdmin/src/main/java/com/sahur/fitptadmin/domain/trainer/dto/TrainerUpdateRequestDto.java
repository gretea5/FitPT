package com.sahur.fitptadmin.domain.trainer.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerUpdateRequestDto {
    private String trainerName;
    private String trainerLoginId;
    private String trainerPw;
}
