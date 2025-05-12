package com.ssafy.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.usecase.user.GetUserInfoUsecase
import com.ssafy.domain.usecase.user.UpdateUserInfoUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUsecase,
    private val updateUserInfoUsecase: UpdateUserInfoUsecase,
    private val dataStoreSource: UserDataStoreSource
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfoState>(UserInfoState.Initial)
    val userInfo: StateFlow<UserInfoState> = _userInfo.asStateFlow()

    private val _temporaryUserInfo = MutableStateFlow<UserInfo?>(null)
    val temporaryUserInfo: StateFlow<UserInfo?> = _temporaryUserInfo.asStateFlow()

    fun setLoading() {
        _userInfo.value = UserInfoState.Loading
    }

    fun fetchUser() {
        viewModelScope.launch {
            getUserInfoUseCase()
                .onStart { setLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            val user = uiState.data
                            _userInfo.value = UserInfoState.Success(uiState.data)
                            saveUserInfoToDataStore(user)
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

    fun updateUser(userInfo: UserInfo) {
        viewModelScope.launch {
            updateUserInfoUsecase(userInfo)
                .onStart { setLoading() }
                .catch { e ->
                    Log.e("UserFragment", "에러 발생: ${e.message}", e)
                }
                .first()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d("UserFragment", "User sdf: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "updateUser: ${_userInfo.value}")
                        }
                    }
                }
        }
    }

    private fun saveUserInfoToDataStore(user: UserInfo) {
        viewModelScope.launch {
            dataStoreSource.saveUser(user)
        }
    }

    fun setTemporaryUserInfo(user: UserInfo) {
        _temporaryUserInfo.value = user
    }

    fun resetTemporaryUserInfo() {
        _temporaryUserInfo.value = null
    }

    fun updateName(name: String) {
        _temporaryUserInfo.value = _temporaryUserInfo.value?.copy(memberName = name)
    }

    fun updateBirth(birth: String){
        _temporaryUserInfo.value = _temporaryUserInfo.value?.copy(memberBirth = birth)
    }

    fun updateGender(gender: String) {
        _temporaryUserInfo.value = _temporaryUserInfo.value?.copy(memberGender = gender)
    }

    fun updateHeight(height: Double) {
        _temporaryUserInfo.value = _temporaryUserInfo.value?.copy(memberHeight = height)
    }

    fun updateWeight(weight: Double) {
        _temporaryUserInfo.value = _temporaryUserInfo.value?.copy(memberWeight = weight)
    }
}

sealed class UserInfoState {
    object Initial: UserInfoState()
    object Loading: UserInfoState()
    data class Success(val userInfo: UserInfo): UserInfoState()
    data class Error(val message: String): UserInfoState()
}

sealed class UserUpdateInfoState {
    object Initial: UserUpdateInfoState()
    object Loading: UserUpdateInfoState()
    data class Success(val success: Int): UserUpdateInfoState()
    data class Error(val message: String): UserUpdateInfoState()
}
