package com.ssafy.domain.usecase.auth

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUsecase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userInfo: UserInfo): Flow<ResponseStatus<JwtToken>> {
        return authRepository.signUp(userInfo)
    }
}