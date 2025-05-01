package com.ssafy.domain.repository

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(accessToken: String): Flow<ResponseStatus<JwtToken>>
}