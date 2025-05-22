package com.ssafy.data.network.request.schedule

data class ScheduleRequest(
    val memberId: Long,
    val trainerId: Long,
    val startTime: String,
    val endTime: String,
    val scheduleContent: String
)