package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "muscle_part")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MusclePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_part_id", nullable = false)
    private Long musclePartId;

    @Column(name = "report_exercise_id", nullable = false)
    private Long reportExerciseId;
}
