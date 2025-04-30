package com.sahur.fitptadmin.domain.trainer.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerResponseDto {
    private Long trainerId;
    private String trainerLoginId;
    private String trainerName;
    private LocalDate trainerBirthday;
}
