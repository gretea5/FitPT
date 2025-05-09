package com.ssafy.presentation.mypage.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.sign.GymInfoItem
import com.ssafy.domain.model.sign.UserInfo
import com.ssafy.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val dataStore: UserDataStoreSource
) : ViewModel() {
    //체육관 저장
    private val _selectedGym = MutableStateFlow<GymInfoItem?>(null)
    val selectedGym: StateFlow<GymInfoItem?> = _selectedGym.asStateFlow()

    private val _userJoin = MutableStateFlow(
        UserInfo()
    )
    val userJoin = _userJoin.asStateFlow()


    fun setGym(gym: GymInfoItem) {
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

sealed class MypageStatus {
    object Idle : MypageStatus()
    object Success : MypageStatus()
    data class Error(val message: String) : MypageStatus()
}