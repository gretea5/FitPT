package com.ssafy.data.repository.auth

import android.util.Log
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.request.RegisterUserInfoRequest
import com.ssafy.data.network.request.UserLoginRequest
import com.ssafy.data.network.response.JwtTokenResponse.Companion.toDomainModel
import com.ssafy.data.network.response.RegisterUserResponse.Companion.toDomainModel
import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "AuthRepositoryImpl"
internal class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStore: UserDataStoreSource
): AuthRepository {
    override suspend fun login(): Flow<ResponseStatus<JwtToken>> {
        return flow {
            ApiResponseHandler().handle {
                val kakaoAccess = dataStore.kakaoAccessToken.first()!!
                val fcmToken = dataStore.fcmToken.first()!!
                Log.d(TAG,kakaoAccess)
                Log.d(TAG,fcmToken)
                authService.login(
                    UserLoginRequest(
                        kakaoAccessToken = kakaoAccess,
                        fcmToken = fcmToken
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        // dataStore.saveUserId(result.data.userId.toLong())
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        Log.d(TAG,result.error.toDomainModel().toString())
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun signUp(userInfo: UserInfo): Flow<ResponseStatus<JwtToken>> {
        return flow {
            ApiResponseHandler().handle {
                val kakaoAccess = dataStore.kakaoAccessToken.first()!!
                val fcmToken = dataStore.fcmToken.first()!!
                val memberName = dataStore.nickname.first()!!

                Log.d(TAG, "userInfo: $userInfo")
                Log.d(TAG, "kakaoAccessToken: $kakaoAccess")
                Log.d(TAG, "fcmToken: $fcmToken")
                Log.d(TAG, "memberName: $memberName")

                authService.signUp(
                    RegisterUserInfoRequest(
                        kakaoAccessToken = kakaoAccess,
                        fcmToken = fcmToken,
                        adminId = userInfo.admin,
                        memberBirth = userInfo.memberBirth,
                        memberGender = userInfo.memberGender,
                        memberHeight = userInfo.memberHeight,
                        memberWeight = userInfo.memberWeight,
                        memberName = memberName
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        Log.d(TAG, "회원가입 성공: ${result.data}")
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        Log.e(TAG, "회원가입 실패 - 코드: ${result.error.code}, 메시지: ${result.error.message}")
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}