package com.ssafy.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.model.schedule.ScheduleWithMemberInfo
import com.ssafy.domain.usecase.member.GetMemberInfoByIdUseCase
import com.ssafy.domain.usecase.schedule.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel_ssafy"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getMemberInfoByIdUseCase: GetMemberInfoByIdUseCase,
    private val dataStore: TrainerDataStoreSource
) : ViewModel(){

    private val _homeState = MutableStateFlow<HomeStatus>(HomeStatus.Idle)
    val homeState : StateFlow<HomeStatus> = _homeState.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _memberInfoCache = mutableMapOf<Long, MemberInfo>()

    private val _scheduleItems = MutableStateFlow<List<ScheduleWithMemberInfo>>(emptyList())
    val scheduleItems: StateFlow<List<ScheduleWithMemberInfo>> = _scheduleItems.asStateFlow()

    private val _monthlyScheduleItems = MutableStateFlow<List<Schedule>>(emptyList())
    val monthlyScheduleItems: StateFlow<List<Schedule>> = _monthlyScheduleItems.asStateFlow()

    fun getMonthlySchedules(month: String) {
        viewModelScope.launch {
            try {
                val storedTrainerId = dataStore.trainerId.first()

                getScheduleUseCase(null, month, storedTrainerId, null).collect { response ->
                    Log.d(TAG, "getMonthlySchedules 스케쥴 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _monthlyScheduleItems.value = response.data
                            _homeState.value = HomeStatus.Success(response.data)
                        }
                        is ResponseStatus.Error -> {
                            _homeState.value = HomeStatus.Error(response.error.message)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
                _homeState.value = HomeStatus.Error("서버와의 연결에 실패했습니다.")
            }

        }
    }

    fun getSchedules(date: String?, month: String?, trainerId: Long?, memberId: Long?) {
        viewModelScope.launch { 
            try {
                val storedTrainerId = dataStore.trainerId.first()

                getScheduleUseCase(date, month, storedTrainerId, memberId).collect { response ->
                    Log.d(TAG, "스케쥴 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _schedules.value = response.data
                            _homeState.value = HomeStatus.Success(response.data)

                            fetchMemberInfoForSchedules(response.data)
                        }
                        is ResponseStatus.Error -> {
                            _homeState.value = HomeStatus.Error(response.error.message)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
                _homeState.value = HomeStatus.Error("서버와의 연결에 실패했습니다.")
            }

        }
    }

    private fun fetchMemberInfoForSchedules(schedules: List<Schedule>) {
        viewModelScope.launch {
            val memberIds = schedules.map { it.memberId }.distinct()

            val idsToFetch = memberIds.filter { !_memberInfoCache.containsKey(it) }

            Log.d(TAG, "fetchMemberInfoForSchedules: ${idsToFetch.joinToString(", ")}")

            val deferredResults = idsToFetch.map { memberId ->
                async {
                    try {
                        getMemberInfoByIdUseCase(memberId).first().let { response ->
                            when(response) {
                                is ResponseStatus.Success -> {
                                    _memberInfoCache[memberId] = response.data
                                }
                                is ResponseStatus.Error -> {
                                    Log.e(TAG, "ResponseStatus Error: ${response.error.message}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "사용자 정보 조회 실패: $memberId, ${e.message}")
                    }
                }
            }

            deferredResults.awaitAll()

            Log.d(TAG, "캐시된 멤버 정보: ${_memberInfoCache.keys.joinToString()}")
            Log.d(TAG, "캐시 상태: ${_memberInfoCache.entries.joinToString { "${it.key}=${it.value.memberName}" }}")

            val items = schedules.map { schedule ->
                val memberName = schedule.memberId.let { _memberInfoCache[it]?.memberName  }
                    ?: "알 수 없음"
                ScheduleWithMemberInfo(
                    memberName = memberName,
                    scheduleId = schedule.scheduleId,
                    startTime = schedule.startTime,
                    endTime = schedule.endTime,
                    memberId = schedule.memberId,
                    trainerId = schedule.trainerId,
                    scheduleContent = schedule.scheduleContent
                )
            }

            _scheduleItems.value = items
        }
    }


    private fun resetHomeState() {
        _homeState.value = HomeStatus.Idle
    }
}

sealed class HomeStatus {
    object Idle : HomeStatus()
    data class Success(val schedules: List<Schedule>) : HomeStatus()
    data class Error(val message: String) : HomeStatus()
}