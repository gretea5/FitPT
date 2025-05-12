package com.ssafy.presentation.login.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.usercase.auth.LoginUseCase
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
    private val dataStore: TrainerDataStoreSource
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val loginState : StateFlow<LoginStatus> = _loginState.asStateFlow()

    fun login(trainerLoginId: String, trainerPw: String) {
        viewModelScope.launch {
            try {
                loginUseCase(trainerLoginId, trainerPw).collect { response ->
                    Log.d(TAG, "login: $response")
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
}

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}