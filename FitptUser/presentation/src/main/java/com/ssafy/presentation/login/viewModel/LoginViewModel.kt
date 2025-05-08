package com.ssafy.presentation.login.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.sign.GymInfoItem
import com.ssafy.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val dataStore: UserDataStoreSource
) : ViewModel() {
    // 로그인 상태 Flow (이전 값을 유지하지 않음)
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.Idle) // 🔥 null 기본값 추가
    val loginState : StateFlow<LoginStatus> = _loginState.asStateFlow()
    //체육관 저장
    private val _selectedGym = MutableStateFlow<GymInfoItem?>(null)
    val selectedGym: StateFlow<GymInfoItem?> = _selectedGym.asStateFlow()



    fun login(accessToken: String) {
        viewModelScope.launch {
            try {
                loginUseCase(accessToken).collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
                            _loginState.value = LoginStatus.Success
                            //dataStore.saveJwtToken("Bearer " + response.data.accessToken)
                        }
                        is ResponseStatus.Error -> {
                            _loginState.value = LoginStatus.Error("로그인에 실패했습니다: ${response.error.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
                _loginState.value = LoginStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginStatus.Idle
    }

    fun setGym(gym: GymInfoItem) {
        _selectedGym.value = gym
    }
}

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}