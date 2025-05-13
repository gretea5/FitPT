package com.ssafy.data.repository.schedule

import com.ssafy.data.network.api.ScheduleService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.response.ScheduleResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ScheduleRepositoryImpl_ssafy"

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleService : ScheduleService
) : ScheduleRepository
{
    override suspend fun getSchedules(
        date: String?,
        month: String?,
        trainerId: Long?,
        memberId: Long?
    ): Flow<ResponseStatus<List<Schedule>>> {
        return flow {
            ApiResponseHandler().handle {
                scheduleService.getSchedules(
                    date = date,
                    month = month,
                    trainerId = trainerId,
                    memberId = memberId
                )
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.map { scheduleResponse ->
                            scheduleResponse.toDomainModel()
                        }))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}