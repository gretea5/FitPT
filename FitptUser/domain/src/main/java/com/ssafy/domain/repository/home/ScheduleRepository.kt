package com.ssafy.domain.repository.home

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.home.ScheduleInfo
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun getSchedules(date: String,month: String): Flow<ResponseStatus<List<ScheduleInfo>>>
}