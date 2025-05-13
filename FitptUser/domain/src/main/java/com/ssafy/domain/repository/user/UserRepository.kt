package com.ssafy.domain.repository.user

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(): Flow<ResponseStatus<UserInfo>>
    suspend fun updateUserInfo(userInfo: UserInfo): Flow<ResponseStatus<Unit>>
}