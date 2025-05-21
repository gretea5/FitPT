package com.ssafy.domain.repository.auth

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(): Flow<ResponseStatus<JwtToken>>
    suspend fun signUp(userInfo: UserInfo) : Flow<ResponseStatus<JwtToken>>
}