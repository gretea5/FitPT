package com.ssafy.data.repository.home

import android.util.Log
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.ScheduleService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.response.GetUserInfoResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.home.ScheduleInfo
import com.ssafy.domain.repository.home.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


internal class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleService: ScheduleService,
    private val dataStore: UserDataStoreSource
): ScheduleRepository{
    override suspend fun getSchedules(date: String, month: String): Flow<ResponseStatus<List<ScheduleInfo>>> {
        return flow {
            val result = ApiResponseHandler().handle {
                scheduleService.getSchedules(date,month)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    //Log.d(TAG, "유저 정보 가져오기 성공: ${result.data}")
                    emit(ResponseStatus.Success(result.data))
                }
                is ApiResponse.Error -> {
                    //Log.e(TAG, "유저 정보 가져오기 실패 - 코드: ${result.error.code}, 메시지: ${result.error.message}")
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }
}