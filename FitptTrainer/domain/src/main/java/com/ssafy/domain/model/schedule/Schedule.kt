package com.ssafy.domain.model.schedule

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Schedule(
    val scheduleId: Long,
    val memberId: Long?,
    val trainerId: Long?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val scheduleContent: String?
) : BaseModel
