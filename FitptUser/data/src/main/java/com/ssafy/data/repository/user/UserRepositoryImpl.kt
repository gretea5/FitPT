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
                userService.getUserInfo(3)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(result.data.toDomainModel()))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun updateUserInfo(
        userInfo: UserInfo): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val memberId = dataStore.user.firstOrNull()!!.memberId
                userService.updateUserInfo(memberId,
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
                val memberId = dataStore.user.firstOrNull()!!.memberId
                userService.deleteUserInfo(memberId)
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