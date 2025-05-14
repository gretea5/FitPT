package com.ssafy.domain.model.schedule

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleWithMemberInfo(
    val scheduleId: Long,
    val memberId: Long,
    val memberName: String,
    val trainerId: Long,
    val startTime: String,
    val endTime: String,
    val scheduleContent: String,
) : BaseModel
