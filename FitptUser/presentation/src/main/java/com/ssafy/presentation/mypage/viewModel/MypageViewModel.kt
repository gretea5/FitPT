package com.ssafy.presentation.mypage.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.usecase.user.GetUserInfoUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val dataStore: UserDataStoreSource,
    private val getUserInfoUsecase: GetUserInfoUsecase
) : ViewModel() {
    //체육관 저장
    private val _selectedGym = MutableStateFlow<Gym?>(null)
    val selectedGym: StateFlow<Gym?> = _selectedGym.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfoState>(UserInfoState.Initial)
    val userInfo: StateFlow<UserInfoState> = _userInfo.asStateFlow()

    fun setLoading() {
        _userInfo.value = UserInfoState.Loading
    }

    fun fetchUser() {
        viewModelScope.launch {
            getUserInfoUsecase()
                .onStart { setLoading() }
                .catch { e ->

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

    fun setGym(gym: Gym) {
        _selectedGym.value = gym
    }
}

sealed class UserInfoState {
    object Initial: UserInfoState()
    object Loading: UserInfoState()
    data class Success(val userInfo: UserInfo): UserInfoState()
    data class Error(val message: String): UserInfoState()
}