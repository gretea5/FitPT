package com.ssafy.data.network.response

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.schedule.Schedule
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleResponse(
    val scheduleId: Long,
    val memberId: Long,
    val trainerId: Long,
    val startTime: String,
    val endTime: String,
    val scheduleContent: String
) : BaseResponse {
    companion object : DataMapper<ScheduleResponse, Schedule> {
        override fun ScheduleResponse.toDomainModel(): Schedule {
            return Schedule(
                scheduleId = this.scheduleId,
                memberId = this.memberId,
                trainerId = this.trainerId,
                startTime = this.startTime,
                endTime = this.endTime,
                scheduleContent = this.scheduleContent
            )
        }
    }
}
