package com.ssafy.domain.usecase.auth

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<ResponseStatus<JwtToken>> {
        return authRepository.login()
    }
}