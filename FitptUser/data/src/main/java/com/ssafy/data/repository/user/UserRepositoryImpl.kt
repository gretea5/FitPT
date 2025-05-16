package com.ssafy.data.repository.user

import android.util.Log
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.api.UserService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.request.UserLoginRequest
import com.ssafy.data.network.request.UserRequest
import com.ssafy.data.network.response.GetUserInfoResponse.Companion.toDomainModel
import com.ssafy.data.network.response.JwtTokenResponse.Companion.toDomainModel
import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "UserRepositoryImpl"
internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val dataStore: UserDataStoreSource
): UserRepository {

    override suspend fun getUserInfo(): Flow<ResponseStatus<UserInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val memberId = dataStore.userId.first()!!.toInt()
                userService.getUserInfo(memberId)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d(TAG, "유저 정보 가져오기 성공: ${result.data}")
                    emit(ResponseStatus.Success(result.data.toDomainModel()))
                }
                is ApiResponse.Error -> {
                    Log.e(TAG, "유저 정보 가져오기 실패 - 코드: ${result.error.code}, 메시지: ${result.error.message}")
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun updateUserInfo(
        userInfo: UserInfo): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val memberId = dataStore.user.firstOrNull()!!.memberId
                Log.d(TAG,"일정"+memberId.toString())
                userService.updateUserInfo(
                    UserRequest(
                        adminId = userInfo.admin.toLong(),
                        memberBirth = userInfo.memberBirth,
                        memberGender = userInfo.memberGender,
                        memberHeight = userInfo.memberHeight,
                        memberId = memberId.toLong(),
                        memberName = userInfo.memberName,
                        memberWeight = userInfo.memberWeight,
                        trainerId = userInfo.trainerId.toLong()
                    )
                )
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(Unit))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun deleteUserInfo(): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                userService.deleteUserInfo()
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(Unit))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    /*override suspend fun login(accessToken: String): Flow<ResponseStatus<JwtToken>> {
        return flow {
            ApiResponseHandler().handle {
                authService.login(
                    UserLoginRequest(
                        kakaoAccessToken = accessToken,
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        // dataStore.saveUserId(result.data.userId.toLong())
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }*/
}