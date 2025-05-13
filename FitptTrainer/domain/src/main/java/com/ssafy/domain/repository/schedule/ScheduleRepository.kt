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
    ): Flow<ResponseStatus<List<Schedule>>>
}