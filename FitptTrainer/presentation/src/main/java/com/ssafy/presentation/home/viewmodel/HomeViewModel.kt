package com.ssafy.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.usercase.schedule.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val dataStore: TrainerDataStoreSource
) : ViewModel(){
    private val _homeState = MutableStateFlow<HomeStatus>(HomeStatus.Idle)
    val homeState : StateFlow<HomeStatus> = _homeState.asStateFlow()

    fun getSchedules(date: String?, month: String?, trainerId: Long?, memberId: Long?) {
        viewModelScope.launch { 
            try {
                val storedTrainerId = dataStore.trainerId.first()

                getScheduleUseCase(date, month, storedTrainerId, memberId).collect { response ->
                    Log.d(TAG, "스케쥴 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
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

    private fun resetHomeState() {
        _homeState.value = HomeStatus.Idle
    }
}

sealed class HomeStatus {
    object Idle : HomeStatus()
    data class Success(val schedules: List<Schedule>) : HomeStatus()
    data class Error(val message: String) : HomeStatus()
}