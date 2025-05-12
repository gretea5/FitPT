package com.ssafy.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.usecase.gym.GetGymListUsecase
import com.ssafy.domain.usecase.user.GetUserInfoUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GymInfoViewModel  @Inject constructor(
    private val getGymListUsecase: GetGymListUsecase,
    private val dataStoreSource: UserDataStoreSource
) : ViewModel() {

    private val _gymInfo = MutableStateFlow<GymInfoState>(GymInfoState.Initial)
    val gymInfo: StateFlow<GymInfoState> = _gymInfo.asStateFlow()

    private val _tempgymInfo = MutableStateFlow<Gym?>(null)
    val tempgymInfo: StateFlow<Gym?> = _tempgymInfo.asStateFlow()

    fun setLoading() {
        _gymInfo.value = GymInfoState.Loading
    }

    fun getGymList(keyword: String) {
        viewModelScope.launch {
            getGymListUsecase(keyword)
                .onStart { setLoading() }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            val user = uiState.data
                            _gymInfo.value = GymInfoState.Success(uiState.data)
                            //Log.d("UserFragment", "User: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _gymInfo.value = GymInfoState.Error(uiState.error.message)
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }

    fun gymListClear(){
        _gymInfo.value = GymInfoState.Initial
    }

    fun updateClick(gym: Gym){
        _tempgymInfo.value = gym
    }

    fun tempGymClear(){
        _tempgymInfo.value = null
    }
}
sealed class GymInfoState {
    object Initial: GymInfoState()
    object Loading: GymInfoState()
    data class Success(val gymList: List<Gym>): GymInfoState()
    data class Error(val message: String): GymInfoState()
}