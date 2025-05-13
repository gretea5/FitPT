package com.ssafy.data.network.response

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.schedule.Schedule
import java.time.LocalDateTime
import kotlinx.parcelize.Parcelize
import java.time.format.DateTimeFormatter

@Parcelize
data class ScheduleResponse(
    @SerializedName("scheduleId") val scheduleId: Long,
    @SerializedName("memberId") val memberId: Long?,
    @SerializedName("trainerId") val trainerId: Long?,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("scheduleContent") val scheduleContent: String?
) : BaseResponse {
    companion object: DataMapper<ScheduleResponse, Schedule> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun ScheduleResponse.toDomainModel(): Schedule {
            return Schedule(
                scheduleId = scheduleId,
                memberId = memberId,
                trainerId = trainerId,
                startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME),
                endTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME),
                scheduleContent = scheduleContent
            )
        }
    }
}
