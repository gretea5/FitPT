package com.ssafy.data.repository.schedule

import android.util.Log
import com.ssafy.data.network.api.ScheduleService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.request.schedule.ScheduleRequest
import com.ssafy.data.network.response.schedule.ScheduleResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    override suspend fun createSchedule(
        memberId: Long?,
        startTime: String?,
        endTime: String?,
        scheduleContent: String?,
        trainerId: Long?
    ): Flow<ResponseStatus<Long>> {
        return flow {
            ApiResponseHandler().handle {
                scheduleService.createSchedule(
                    ScheduleRequest(
                        memberId = memberId!!,
                        startTime = startTime!!,
                        endTime = endTime!!,
                        scheduleContent = scheduleContent!!,
                        trainerId = trainerId!!
                    )
                )
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun updateSchedule(
        scheduledId: Long,
        memberId: Long?,
        trainerId: Long?,
        startTime: String?,
        endTime: String?,
        scheduleContent: String?
    ): Flow<ResponseStatus<Long>> {
        return ApiResponseHandler().handle {
            scheduleService.updateSchedule(
                scheduledId,
                ScheduleRequest(
                    trainerId = trainerId!!,
                    memberId = memberId!!,
                    startTime = startTime!!,
                    endTime = endTime!!,
                    scheduleContent = scheduleContent!!
                )
            )
        }.map { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Log.d(TAG, "updateSchedule: ${result.data}")
                    ResponseStatus.Success(result.data)
                }
                is ApiResponse.Error -> {
                    Log.d(TAG, "updateSchedule: ${result.error.toDomainModel().error}")
                    Log.d(TAG, "updateSchedule: ${result.error.toDomainModel().message}")
                    ResponseStatus.Error(result.error.toDomainModel())
                }
            }
        }
    }

    override suspend fun deleteSchedule(scheduleId: Long): Flow<ResponseStatus<Long>> {
        Log.d(TAG, "deleteSchedule: $scheduleId")
        return ApiResponseHandler().handle {
            Log.d(TAG, "deleteSchedule handle: $scheduleId")
            scheduleService.deleteSchedule(scheduleId)
        }.map { result ->
            Log.d(TAG, "deleteSchedule map: $result")
            when (result) {
                is ApiResponse.Success -> ResponseStatus.Success(result.data)
                is ApiResponse.Error -> ResponseStatus.Error(result.error.toDomainModel())
            }
        }
    }
}