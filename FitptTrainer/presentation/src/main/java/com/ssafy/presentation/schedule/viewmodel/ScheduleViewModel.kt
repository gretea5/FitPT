package com.ssafy.presentation.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.usercase.schedule.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getScheduleUseCase: GetScheduleUseCase
) : ViewModel() {
    private val _scheduleState = MutableStateFlow<ScheduleStatus>(ScheduleStatus.Idle)
    val scheduleState: StateFlow<ScheduleStatus> = _scheduleState.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

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
}

sealed class ScheduleStatus {
    object Idle : ScheduleStatus()
    object Success : ScheduleStatus()
    data class Error(val message: String) : ScheduleStatus()
}