package com.ssafy.presentation.member.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.model.report.ReportList
import com.ssafy.domain.usecase.measure.GetBodyListUsecase
import com.ssafy.domain.usecase.member.GetMemberInfoByIdUseCase
import com.ssafy.domain.usecase.member.GetMembersUseCase
import com.ssafy.domain.usecase.report.GetReportListUsecase
import com.ssafy.presentation.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserWorkoutInfoViewMode_ssafy"

@HiltViewModel
class UserWorkoutInfoViewModel @Inject constructor(
    private val getMembersUseCase: GetMembersUseCase,
    private val getReportListUseCase: GetReportListUsecase,
    private val getMemberInfoByIdUseCase: GetMemberInfoByIdUseCase,
    private val getBodyListUseCase: GetBodyListUsecase
) : ViewModel() {
    private val _workoutState = MutableStateFlow<UserWorkoutInfoStatus>(UserWorkoutInfoStatus.Idle)
    val workoutState: StateFlow<UserWorkoutInfoStatus> = _workoutState.asStateFlow()

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members.asStateFlow()

    private val _member = MutableStateFlow<MemberInfo?>(null)
    val member: StateFlow<MemberInfo?> = _member.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportList>>(emptyList())
    val reports: StateFlow<List<ReportList>> = _reports.asStateFlow()

    private val _filteredReports = MutableStateFlow<List<ReportList>>(emptyList())
    val filteredReports = _filteredReports.asStateFlow()

    private val _composition = MutableStateFlow<List<CompositionItem>>(emptyList())
    val composition: StateFlow<List<CompositionItem>> = _composition.asStateFlow()

    private var allReports = listOf<ReportList>()
    private var selectedYear: Int = TimeUtils.getCurrentYear()
    private var selectedMonth: Int = 0 // 0은 전체 월을 의미

    fun getMembers() {
        viewModelScope.launch {
            try {
                getMembersUseCase().collect { response ->
                    Log.d(TAG, "getMembers 회원 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _members.value = response.data
                            _workoutState.value = UserWorkoutInfoStatus.Success
                        }

                        is ResponseStatus.Error -> {
                            _workoutState.value = UserWorkoutInfoStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getMembers exception ${e.message}")
                _workoutState.value = UserWorkoutInfoStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun getReports(memberId : Int) {
        viewModelScope.launch {
            try {
                getReportListUseCase(memberId = memberId).collect { response ->
                    Log.d(TAG, "getReports: ${response}")

                    when (response) {
                        is ResponseStatus.Success -> {
                            _reports.value = response.data
                            allReports = response.data
                            filterReportsByYearAndMonth()
                            _workoutState.value = UserWorkoutInfoStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _workoutState.value = UserWorkoutInfoStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getMembers exception ${e.message}")
                _workoutState.value = UserWorkoutInfoStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun filterReportsByYearAndMonth() {
        viewModelScope.launch {
            val filtered = allReports.filter { report ->
                val reportDate = TimeUtils.parseDate(report.createdAt)
                val reportYear = TimeUtils.getYearFromDate(reportDate)
                val reportMonth = TimeUtils.getMonthFromDate(reportDate)

                (reportYear == selectedYear) && (selectedMonth == 0 || reportMonth == selectedMonth)
            }
            _filteredReports.value = filtered.toList()
        }
    }

    fun setSelectedYear(year: Int) {
        setSelectedMonth("전체")
        selectedYear = year
        filterReportsByYearAndMonth()
    }

    fun setSelectedMonth(monthText: String) {
        selectedMonth = when (monthText) {
            "전체" -> 0 // 전체 월을 의미하는 0
            else -> {
                // "1월", "2월" 등에서 숫자만 추출
                monthText.replace("월", "").toInt()
            }
        }
        filterReportsByYearAndMonth()
    }

    fun getMember(memberId: Long) {
        viewModelScope.launch {
            try {
                getMemberInfoByIdUseCase(memberId = memberId).collect { response ->
                    Log.d(TAG, "getMember: ${response}")

                    when(response) {
                        is ResponseStatus.Success -> {
                            _member.value = response.data
                            _workoutState.value = UserWorkoutInfoStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _workoutState.value =
                                UserWorkoutInfoStatus.Error(response.error.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "getMembers exception ${e.message}")
                _workoutState.value = UserWorkoutInfoStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun getComposition(memberId: Long) {
        viewModelScope.launch {
            try {
                getBodyListUseCase(
                    memberId = memberId.toInt(),
                    sort = "createdAt",
                    order = "asc"
                ).collect { response ->
                    Log.d(TAG, "getComposition: ${response}")

                    when(response) {
                        is ResponseStatus.Success -> {
                            _composition.value = response.data
                            _workoutState.value = UserWorkoutInfoStatus.Success
                        }
                        is ResponseStatus.Error -> {
                            _workoutState.value =
                                UserWorkoutInfoStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getComposition exception ${e.message}")
                _workoutState.value = UserWorkoutInfoStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }
}

sealed class UserWorkoutInfoStatus {
    object Idle : UserWorkoutInfoStatus()
    object Success : UserWorkoutInfoStatus()
    data class Error(val message: String) : UserWorkoutInfoStatus()
}