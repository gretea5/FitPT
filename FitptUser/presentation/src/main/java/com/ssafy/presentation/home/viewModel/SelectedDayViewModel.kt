package com.ssafy.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class SelectedDayViewModel @Inject constructor(
): ViewModel() {
    private val _selectedDay = MutableStateFlow<SelectedDayState>(SelectedDayState.Initial)
    val selectedDay = _selectedDay.asStateFlow()
    private var selectDayJob: Job? = null
    private val _openDialog = Channel<OpenDialogState>(Channel.RENDEZVOUS)
    val openDialog = _openDialog.receiveAsFlow()

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth.asStateFlow()


    fun setYearMonth(newYearMonth: YearMonth) {
        viewModelScope.launch {
            _selectedYearMonth.update { newYearMonth }
           // Log.d(TAG, "setYearMonth: newYearMonth ${newYearMonth.year} ${newYearMonth.monthValue}")
            //getTotalPayment(newYearMonth.year, newYearMonth.monthValue)
        }
    }

    fun initYearMonth() {
        _selectedYearMonth.value = YearMonth.now()
    }



    fun setSelectedDay(day: LocalDate) {
        selectDayJob?.cancel()

        selectDayJob = viewModelScope.launch {
            // 날짜에 저장된 내역이 있는 경우에만 Exist로 설정
            // 임시로 모두 Exist 처리
            _selectedDay.value = SelectedDayState.Exist(day)
            _openDialog.trySend(OpenDialogState.Opened)
        }
    }

    fun clearSelectedDay() {
        _selectedDay.value = SelectedDayState.Initial
    }
}

sealed class SelectedDayState {
    object Initial: SelectedDayState()
    data class Selected(val day: LocalDate): SelectedDayState()
    data class Exist(val day: LocalDate): SelectedDayState() // 결제 내역이 있는 날
}

sealed class OpenDialogState {
    object Initial: OpenDialogState()
    object Opened: OpenDialogState()
    object Error: OpenDialogState()
}