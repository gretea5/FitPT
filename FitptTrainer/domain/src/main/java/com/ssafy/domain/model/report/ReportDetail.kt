package com.ssafy.domain.model.report

import com.ssafy.domain.model.measure.CompositionItem

data class ReportDetail(
    val reportId: Int,
    val memberId: Int,
    val trainerName: String,
    val createdAt: String,
    val reportComment: String,
    val compositionLog: CompositionItem,
    val reportExercises: List<HealthReportWorkout>
)