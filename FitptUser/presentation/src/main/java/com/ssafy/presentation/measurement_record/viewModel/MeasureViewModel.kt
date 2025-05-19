package com.ssafy.presentation.measurement_record.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionDetailInfo
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.model.measure_record.MesureDetail
import com.ssafy.domain.usecase.measure.CreateBodyUsecase
import com.ssafy.domain.usecase.measure.GetBodyDetailUsecase
import com.ssafy.domain.usecase.measure.GetBodyListUsecase
import com.ssafy.domain.usecase.user.GetUserInfoUsecase
import com.ssafy.presentation.mypage.viewModel.UserInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MeasureViewModel"
@HiltViewModel
class MeasureViewModel @Inject constructor(
    private val dataStore: UserDataStoreSource,
    private val getBodyListUsecase: GetBodyListUsecase,
    private val createBodyUsecase: CreateBodyUsecase,
    private val getBodyDetailUsecase: GetBodyDetailUsecase
) : ViewModel() {
    private val _getBodyListInfo = MutableStateFlow<GetBodyListInfoState>(GetBodyListInfoState.Initial)
    val getBodyListInfo: StateFlow<GetBodyListInfoState> = _getBodyListInfo.asStateFlow()

    private val _getBodyDetailInfo = MutableStateFlow<GetBodyDetailInfoState>(GetBodyDetailInfoState.Initial)
    val getBodyDetailInfo: StateFlow<GetBodyDetailInfoState> = _getBodyDetailInfo.asStateFlow()

    private val _createBodyInfo = MutableStateFlow<CreateBodyInfoState>(CreateBodyInfoState.Initial)
    val createBodyInfo: StateFlow<CreateBodyInfoState> = _createBodyInfo.asStateFlow()

    private val _measureDetailInfo = MutableStateFlow<MesureDetail?>(null)
    val measureDetailInfo: StateFlow<MesureDetail?> = _measureDetailInfo.asStateFlow()


    fun setLoading() {
        _getBodyListInfo.value = GetBodyListInfoState.Loading
    }
    fun setBodyDetailLoading() {
        _getBodyDetailInfo.value = GetBodyDetailInfoState.Loading
    }

    fun getBodyList(sort: String,order: String) {
        viewModelScope.launch {
            getBodyListUsecase(sort,order)
                .onStart { setLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _getBodyListInfo.value = GetBodyListInfoState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message)
                            _getBodyListInfo.value =
                                GetBodyListInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("MeasureViewModel", "fetchUser: else error")
                    }
                }
        }
    }


    fun createBody(userDetail: CompositionDetail, onFinished: () -> Unit) {
        viewModelScope.launch {
            createBodyUsecase(userDetail)
                .onStart { setLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _createBodyInfo.value = CreateBodyInfoState.Success(uiState.data)
                            onFinished()
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message)
                            _createBodyInfo.value =
                                CreateBodyInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("MeasureViewModel", "fetchUser: else error")
                    }
                }
        }
    }

    fun getBodyDetailInfo(compositionLog: Int) {
        viewModelScope.launch {
            getBodyDetailUsecase(compositionLog)
                .onStart { setBodyDetailLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _getBodyDetailInfo.value = GetBodyDetailInfoState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message)
                            _getBodyDetailInfo.value =
                                GetBodyDetailInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("MeasureViewModel", "fetchUser: else error")
                    }
                }
        }
    }

    fun setMeasureDetailInfo(measureDetail: MesureDetail) {
        _measureDetailInfo.value = measureDetail
    }

    fun resetMeasureDetailInfo() {
        _measureDetailInfo.value = null
    }
}

sealed class GetBodyListInfoState {
    object Initial: GetBodyListInfoState()
    object Loading: GetBodyListInfoState()
    data class Success(val getBodyList: List<CompositionItem>): GetBodyListInfoState()
    data class Error(val message: String): GetBodyListInfoState()
}

sealed class GetBodyDetailInfoState {
    object Initial: GetBodyDetailInfoState()
    object Loading: GetBodyDetailInfoState()
    data class Success(val getBodydetail: CompositionDetailInfo): GetBodyDetailInfoState()
    data class Error(val message: String): GetBodyDetailInfoState()
}

sealed class CreateBodyInfoState {
    object Initial: CreateBodyInfoState()
    object Loading: CreateBodyInfoState()
    data class Success(val compositionLog: Int): CreateBodyInfoState()
    data class Error(val message: String): CreateBodyInfoState()
}