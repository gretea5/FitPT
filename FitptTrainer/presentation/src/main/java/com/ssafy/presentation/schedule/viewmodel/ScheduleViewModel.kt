package com.ssafy.presentation.schedule.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.usecase.member.GetMembersUseCase
import com.ssafy.domain.usecase.schedule.CreateScheduleUseCase
import com.ssafy.domain.usecase.schedule.DeleteScheduleUseCase
import com.ssafy.domain.usecase.schedule.GetScheduleUseCase
import com.ssafy.domain.usecase.schedule.UpdateScheduleUseCase
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
    private val getMembersUseCase: GetMembersUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val dataStore: TrainerDataStoreSource
) : ViewModel() {
    private val _scheduleState = MutableStateFlow<ScheduleStatus>(ScheduleStatus.Idle)
    val scheduleState: StateFlow<ScheduleStatus> = _scheduleState.asStateFlow()

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _monthlyScheduleItems = MutableStateFlow<List<Schedule>>(emptyList())
    val monthlyScheduleItems: StateFlow<List<Schedule>> = _monthlyScheduleItems.asStateFlow()

    private val _createdScheduleId = MutableStateFlow<Long?>(null)
    val createdScheduleId: StateFlow<Long?> = _createdScheduleId.asStateFlow()

    private val _updatedScheduleId = MutableStateFlow<Long?>(null)
    val updatedScheduleId: StateFlow<Long?> = _updatedScheduleId.asStateFlow()

    private val _deletedScheduleId = MutableStateFlow<Long?>(null)
    val deletedScheduleId: StateFlow<Long?> = _deletedScheduleId.asStateFlow()

    fun getSchedules(date: String? = null, month: String? = null, trainerId: Long? = null, memberId: Long? = null) {
        viewModelScope.launch {
            val storedTrainerId = dataStore.trainerId.first()
            _scheduleState.value = ScheduleStatus.Idle

            getScheduleUseCase(
                date = date,
                month = month,
                trainerId = storedTrainerId,
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

    fun getMembers() {
        viewModelScope.launch {
            try {
                getMembersUseCase().collect { response ->
                    Log.d(TAG, "getMembers 회원 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _members.value = response.data
                            _scheduleState.value = ScheduleStatus.Success
                        }

                        is ResponseStatus.Error -> {
                            _scheduleState.value = ScheduleStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getMembers exception ${e.message}")
                _scheduleState.value = ScheduleStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun createSchedule(memberId: Long, startTime: String, endTime: String, scheduleContent: String) {
        viewModelScope.launch {
            try {
                val storedTrainerId = dataStore.trainerId.first()

                createScheduleUseCase(
                    memberId,
                    startTime,
                    endTime,
                    scheduleContent,
                    storedTrainerId
                ).collect { response ->
                    Log.d(TAG, "createSchedule: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _createdScheduleId.value = response.data
                            _scheduleState.value = ScheduleStatus.Success
                        }

                        is ResponseStatus.Error -> {
                            _scheduleState.value = ScheduleStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "createSchedule exception ${e.message}")
                _scheduleState.value = ScheduleStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun updateSchedule(
        scheduledId: Long,
        memberId: Long,
        trainerId: Long,
        startTime: String,
        endTime: String,
        scheduleContent: String
    ) {
        Log.d(TAG, "updateSchedule scheduledId ${scheduledId}")

        viewModelScope.launch {
            try {
                updateScheduleUseCase(
                    scheduledId = scheduledId,
                    memberId = memberId,
                    trainerId = trainerId,
                    startTime = startTime,
                    endTime = endTime,
                    scheduleContent = scheduleContent
                ).collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "updateSchedule response ${response.data}")
                            _updatedScheduleId.value = response.data
                            _scheduleState.value = ScheduleStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _scheduleState.value = ScheduleStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "updateSchedule exception ${e.message}")
                _scheduleState.value = ScheduleStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun deleteSchedule(scheduleId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "deleteSchedule: $scheduleId")

                deleteScheduleUseCase(scheduleId).collect { response ->
                    Log.d(TAG, "deleteSchedule: $response")

                    when (response) {
                        is ResponseStatus.Success -> {
                            _deletedScheduleId.value = response.data
                            _scheduleState.value = ScheduleStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _scheduleState.value = ScheduleStatus.Error(response.error.message)
                        }
                    }
                }


            } catch (e: Exception) {
                Log.d(TAG, "deleteSchedule exception ${e.message}")
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