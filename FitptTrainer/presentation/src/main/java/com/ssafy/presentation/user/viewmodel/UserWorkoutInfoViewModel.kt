package com.ssafy.presentation.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.usecase.member.GetMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserWorkoutInfoViewMode_ssafy"

@HiltViewModel
class UserWorkoutInfoViewModel @Inject constructor(
    private val getMembersUseCase: GetMembersUseCase
) : ViewModel() {
    private val _workoutState = MutableStateFlow<UserWorkoutInfoStatus>(UserWorkoutInfoStatus.Idle)
    val workoutState: StateFlow<UserWorkoutInfoStatus> = _workoutState.asStateFlow()

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members.asStateFlow()

    fun getMembers() {
        viewModelScope.launch {
            try {
                getMembersUseCase().collect { response ->
                    Log.d(TAG, "getMembers 회원 조회: $response")
                    when (response) {
                        is ResponseStatus.Success -> {
                            _members.value = response.data
                            _workoutState.value = UserWorkoutInfoStatus.Success
                        }

                        is ResponseStatus.Error -> {
                            _workoutState.value = UserWorkoutInfoStatus.Error(response.error.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getMembers exception ${e.message}")
                _workoutState.value = UserWorkoutInfoStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }
}

sealed class UserWorkoutInfoStatus {
    object Idle : UserWorkoutInfoStatus()
    object Success : UserWorkoutInfoStatus()
    data class Error(val message: String) : UserWorkoutInfoStatus()
}