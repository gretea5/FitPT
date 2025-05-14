package com.ssafy.domain.model.report

data class TempHealthReportWorkout(
    val id: Long,
    val exerciseName: String,
    val exerciseAchievement: String,
    val exerciseComment: String,
    val activationMuscleId: List<Int>
)
