package com.ssafy.presentation.login.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.usecase.auth.LoginUseCase
import com.ssafy.domain.usecase.auth.SignUpUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUsecase: SignUpUsecase,
    private val dataStore: UserDataStoreSource
) : ViewModel() {
    // 로그인 상태 Flow (이전 값을 유지하지 않음)
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.Idle) // 🔥 null 기본값 추가
    val loginState : StateFlow<LoginStatus> = _loginState.asStateFlow()

    private val _signUpSuccess = MutableStateFlow<Boolean?>(null)
    val signUpSuccess: StateFlow<Boolean?> get() = _signUpSuccess

    //체육관 저장
    private val _selectedGym = MutableStateFlow<Gym?>(null)
    val selectedGym: StateFlow<Gym?> = _selectedGym.asStateFlow()

    private val _userJoin = MutableStateFlow(
        UserInfo()
    )
    val userJoin = _userJoin.asStateFlow()

    fun login() {
        viewModelScope.launch {
            try {
                loginUseCase().collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
                            dataStore.saveUserId(response.data.memberId.toLong())
                            dataStore.saveJwtToken("Bearer " + response.data.accessToken)
                            _loginState.value = LoginStatus.Success
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

    fun signUpUser(userInfo: UserInfo) {
        viewModelScope.launch {
            try {
                signUpUsecase(userInfo).collect { response ->
                    Log.d(TAG,userInfo.toString())
                    when (response) {
                        is ResponseStatus.Success -> {
                            dataStore.saveUserId(response.data.memberId.toLong())
                            dataStore.saveJwtToken("Bearer " + response.data.accessToken)
                            _signUpSuccess.value = true
                        }
                        is ResponseStatus.Error -> {
                            _signUpSuccess.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginStatus.Idle
    }

    fun setGym(gym: Gym) {
        _selectedGym.value = gym
    }

    fun updateGym(adminNumber: Int) {
        _userJoin.update { it.copy(admin = adminNumber) }
    }

    fun updateGender(gender: String){
        _userJoin.update{it.copy(memberGender = gender)}
    }

    fun resetClear(){
        _userJoin.value = UserInfo()
        _selectedGym.value = null
    }

}

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}

sealed class SignUpStatus {
    object Idle :SignUpStatus()
    object Success : SignUpStatus()
    data class Error(val message: String) : SignUpStatus()
}