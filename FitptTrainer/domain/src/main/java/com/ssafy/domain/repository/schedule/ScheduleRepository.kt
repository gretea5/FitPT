package com.ssafy.domain.repository.schedule

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun getSchedules(
        date: String? = null,
        month: String? = null,
        trainerId: Long? = null,
        memberId: Long? = null
    ) : Flow<ResponseStatus<List<Schedule>>>

    suspend fun createSchedule(
        memberId: Long? = null,
        startTime: String? = null,
        endTime: String? = null,
        scheduleContent: String? = null,
        trainerId: Long? = null
    ) : Flow<ResponseStatus<Long>>

    suspend fun updateSchedule(
        scheduledId: Long,
        memberId: Long? = null,
        trainerId: Long? = null,
        startTime: String? = null,
        endTime: String? = null,
        scheduleContent: String? = null
    ) : Flow<ResponseStatus<Long>>

    suspend fun deleteSchedule(scheduleId: Long) : Flow<ResponseStatus<Long>>
}