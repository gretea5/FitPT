package com.ssafy.domain.model.schedule

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val scheduleId: Long,
    val memberId: Long,
    val trainerId: Long,
    val startTime: String,
    val endTime: String,
    val scheduleContent: String
) : BaseModel
