package com.ssafy.domain.model.report

data class Report(
    val memberId: Int,
    val compositionLogId: Int,
    val reportComment: String,
    val reportExercises: List<HealthReportWorkout>
)
