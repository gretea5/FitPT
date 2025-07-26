package com.sahur.fitptadmin.domain.trainer.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerRegisterDto {
    private Long adminId;
    private String trainerLoginId;
    private String trainerPw;
    private String trainerName;
    private LocalDate trainerBirthDay;
}
