package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_detail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_exercise_id", nullable = false)
    private Long muscleExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "exercise_level")
    private Integer exerciseLevel;

    @Column(name = "exercise_explanation")
    private String exerciseExplanation;

    @OneToMany(mappedBy = "reportExercise")
    private List<WorkoutMuscle> workoutMuscles = new ArrayList<>();
}
