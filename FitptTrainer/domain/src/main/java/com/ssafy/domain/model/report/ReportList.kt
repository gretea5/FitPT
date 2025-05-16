package com.ssafy.domain.model.report

data class ReportList(
    val reportId: Int,
    val memberId: Int,
    val compositionLogId: Int,
    val trainerName: String,
    val reportComment: String,
    val createdAt: String,
    val reportExercises: List<HealthReportWorkout>
)
