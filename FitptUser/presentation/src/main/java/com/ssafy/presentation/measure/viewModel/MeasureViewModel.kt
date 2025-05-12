package com.ssafy.presentation.measure.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
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
                            _getBodyListInfo.value = GetBodyListInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
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
    data class Success(val getBodydetail: CompositionDetail): GetBodyDetailInfoState()
    data class Error(val message: String): GetBodyDetailInfoState()
}