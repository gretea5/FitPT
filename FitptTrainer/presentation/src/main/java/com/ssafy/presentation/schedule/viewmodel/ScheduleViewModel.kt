package com.ssafy.presentation.schedule.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.usecase.schedule.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ScheduleViewModel_ssafy"

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val dataStore: TrainerDataStoreSource
) : ViewModel() {
    private val _scheduleState = MutableStateFlow<ScheduleStatus>(ScheduleStatus.Idle)
    val scheduleState: StateFlow<ScheduleStatus> = _scheduleState.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _monthlyScheduleItems = MutableStateFlow<List<Schedule>>(emptyList())
    val monthlyScheduleItems: StateFlow<List<Schedule>> = _monthlyScheduleItems.asStateFlow()

    fun getSchedules(date: String? = null, month: String? = null, trainerId: Long? = null, memberId: Long? = null) {
        viewModelScope.launch {
            _scheduleState.value = ScheduleStatus.Idle

            getScheduleUseCase(
                date = date,
                month = month,
                trainerId = trainerId,
                memberId = memberId
            ).collectLatest { response ->
                when (response) {
                    is ResponseStatus.Success -> {
                        _schedules.value = response.data
                        _scheduleState.value = ScheduleStatus.Success
                    }
                    is ResponseStatus.Error -> {
                        _scheduleState.value = ScheduleStatus.Error(response.error.message)
                    }
                }
            }
        }
    }

    fun getMonthlySchedules(month: String) {
        viewModelScope.launch {
            try {
                val storedTrainerId = dataStore.trainerId.first()

                getScheduleUseCase(null, month, storedTrainerId, null).collect { response ->
                    Log.d(TAG, "getMonthlySchedules 스케쥴 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _monthlyScheduleItems.value = response.data
                            _scheduleState.value = ScheduleStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _scheduleState.value = ScheduleStatus.Error(response.error.message)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
                _scheduleState.value = ScheduleStatus.Error("서버와의 연결에 실패했습니다.")
            }

        }
    }
}

sealed class ScheduleStatus {
    object Idle : ScheduleStatus()
    object Success : ScheduleStatus()
    data class Error(val message: String) : ScheduleStatus()
}