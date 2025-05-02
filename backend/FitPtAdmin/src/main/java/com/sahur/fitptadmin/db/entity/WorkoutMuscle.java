package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_muscle")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutMuscle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workMuscleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_exercise_id")
    private ReportExercise reportExercise;

    @Column(name = "activation_muscle_id")
    private Long activationMuscleId;
}
