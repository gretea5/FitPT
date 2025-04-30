package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_muscle")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutMuscle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workMuscleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_exercise_id", nullable = false)
    private ReportExercise reportExercise;

    @Column(name = "activation_muscle_id" , nullable = false)
    private Long activationMuscleId;
}
