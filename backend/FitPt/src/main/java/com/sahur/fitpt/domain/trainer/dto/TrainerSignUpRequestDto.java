package com.sahur.fitpt.domain.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerSignUpRequestDto {
    private Long adminId;
    private String trainerName;
    private String trainerLoginId;
    private String trainerPw;
}
