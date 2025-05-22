package com.ssafy.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionDetailItem
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.usecase.measure.CreateBodyUsecase
import com.ssafy.domain.usecase.measure.GetBodyDetailUsecase
import com.ssafy.domain.usecase.measure.GetBodyListUsecase
import com.ssafy.domain.usecase.member.GetMemberInfoByIdUseCase
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
    private val createBodyUsecase: CreateBodyUsecase,
    private val getMemberInfoByIdUseCase: GetMemberInfoByIdUseCase,
    private val getBodyDetailUsecase: GetBodyDetailUsecase,
) : ViewModel() {

    private val _memberId = MutableStateFlow<Int>(0)
    val memberId: StateFlow<Int> = _memberId.asStateFlow()

    private val _measureCreateInfo = MutableStateFlow<CreateBodyInfoState>(CreateBodyInfoState.Initial)
    val measureCreateInfo: StateFlow<CreateBodyInfoState> = _measureCreateInfo.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfoState>(UserInfoState.Initial)
    val userInfo: StateFlow<UserInfoState> = _userInfo.asStateFlow()

    private val _getBodyDetailInfo = MutableStateFlow<GetBodyDetailInfoState>(GetBodyDetailInfoState.Initial)
    val getBodyDetailInfo: StateFlow<GetBodyDetailInfoState> = _getBodyDetailInfo.asStateFlow()


    fun createBody(userDetail: CompositionDetail, onFinished: () -> Unit) {
        viewModelScope.launch {
            createBodyUsecase(userDetail)
                .onStart {  }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _measureCreateInfo.value = CreateBodyInfoState.Success(uiState.data)
                            onFinished()
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message)
                        }
                        else -> Log.d("MeasureViewModel", "fetchUser: else error")
                    }
                }
        }
    }

    fun fetchUser(memberId: Int) {
        viewModelScope.launch {
           getMemberInfoByIdUseCase(memberId.toLong())
                .onStart {  }
                .catch { e ->
                    Log.e("UserFragment", "에러 발생: ${e.message}", e)
                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _userInfo.value = UserInfoState.Success(uiState.data)
                            Log.d("UserFragment", "User: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "fetchUser: ${_userInfo.value}")
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }

    fun getBodyDetailInfo(compositionLogId: Int) {
        viewModelScope.launch {
            getBodyDetailUsecase(compositionLogId)
                .onStart {  }
                .catch { e ->
                    Log.e("UserFragment", "에러 발생: ${e.message}", e)
                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _getBodyDetailInfo.value = GetBodyDetailInfoState.Success(uiState.data)
                            Log.d("UserFragment", "User: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _getBodyDetailInfo.value = GetBodyDetailInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "fetchUser: ${_userInfo.value}")
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }


    fun updateMember(memberId: Int){
        _memberId.value = memberId
    }

    fun resetCreateBody(){
        _measureCreateInfo.value = CreateBodyInfoState.Initial
    }
}

sealed class CreateBodyInfoState {
    object Initial: CreateBodyInfoState()
    object Loading: CreateBodyInfoState()
    data class Success(val measureCreate: Int): CreateBodyInfoState()
    data class Error(val message: String): CreateBodyInfoState()
}

sealed class UserInfoState {
    object Initial: UserInfoState()
    object Loading: UserInfoState()
    data class Success(val userInfo: MemberInfo): UserInfoState()
    data class Error(val message: String): UserInfoState()
}

sealed class GetBodyDetailInfoState {
    object Initial: GetBodyDetailInfoState()
    object Loading: GetBodyDetailInfoState()
    data class Success(val getBodydetail: CompositionDetailItem): GetBodyDetailInfoState()
    data class Error(val message: String): GetBodyDetailInfoState()
}