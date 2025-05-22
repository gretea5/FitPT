package com.ssafy.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.home.ScheduleInfo
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.usecase.gym.GetGymListUsecase
import com.ssafy.domain.usecase.home.GetScheduleUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleViewModel  @Inject constructor(
    private val getScheduleUsecase: GetScheduleUsecase,
    private val dataStoreSource: UserDataStoreSource
) : ViewModel() {
    private val _scheduleInfo = MutableStateFlow<ScheduleInfoState>(ScheduleInfoState.Initial)
    val scheduleInfo: StateFlow<ScheduleInfoState> = _scheduleInfo.asStateFlow()

    fun getScheduleList(date: String, month: String) {
        viewModelScope.launch {
            getScheduleUsecase(date,month)
                .onStart {  }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _scheduleInfo.value = ScheduleInfoState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            _scheduleInfo.value = ScheduleInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }

}

sealed class ScheduleInfoState {
    object Initial: ScheduleInfoState()
    object Loading: ScheduleInfoState()
    data class Success(val scheduleList: List<ScheduleInfo>): ScheduleInfoState()
    data class Error(val message: String): ScheduleInfoState()
}