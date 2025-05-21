package com.ssafy.presentation.report.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.usecase.measure.CreateBodyUsecase
import com.ssafy.domain.usecase.measure.GetBodyDetailUsecase
import com.ssafy.domain.usecase.measure.GetBodyListUsecase
import com.ssafy.domain.usecase.report.GetReportDetailUsecase
import com.ssafy.domain.usecase.report.GetReportListUsecase
import com.ssafy.presentation.home.viewModel.UserInfoState
import com.ssafy.presentation.measurement_record.viewModel.GetBodyListInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ReportViewModel"
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val dataStore: UserDataStoreSource,
    private val getReportListUsecase: GetReportListUsecase,
    private val getReportDetailUsecase: GetReportDetailUsecase
) : ViewModel() {
    private val _getReportListInfo = MutableStateFlow<ReportListState>(ReportListState.Initial)
    val getReportListInfo: StateFlow<ReportListState> = _getReportListInfo.asStateFlow()

    private val _getReportDetailInfo = MutableStateFlow<ReportDetailState>(ReportDetailState.Initial)
    val getReportDetailInfo: StateFlow<ReportDetailState> = _getReportDetailInfo.asStateFlow()


    private val _selectReport = MutableStateFlow<Int>(0)
    val selectReport: StateFlow<Int> = _selectReport.asStateFlow()

    fun setReportListLoading() {
        _getReportListInfo.value = ReportListState.Loading
    }

    fun setReportDetailLoading() {
        _getReportDetailInfo.value = ReportDetailState.Loading
    }

    fun getReportList() {
        viewModelScope.launch {
            getReportListUsecase()
                .onStart { setReportListLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _getReportListInfo.value = ReportListState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message.toString())
                            _getReportListInfo.value = ReportListState.Error(uiState.error.message)
                        }
                        else ->  Log.d(TAG,"실패")
                    }
                }
        }
    }

    fun getReportDetail(reportId: Int){
        viewModelScope.launch {
            getReportDetailUsecase(reportId)
                .onStart { setReportDetailLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _getReportDetailInfo.value = ReportDetailState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            _getReportDetailInfo.value = ReportDetailState.Error(uiState.error.message)
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }

    fun setReport(reportId: Int) {
        _selectReport.value = reportId
    }

}

sealed class ReportListState {
    object Initial: ReportListState()
    object Loading: ReportListState()
    data class Success(val reportList: List<PtReportItem>): ReportListState()
    data class Error(val message: String): ReportListState()
}

sealed class ReportDetailState {
    object Initial: ReportDetailState()
    object Loading: ReportDetailState()
    data class Success(val reportDetail: ReportDetailInfo): ReportDetailState()
    data class Error(val message: String): ReportDetailState()
}