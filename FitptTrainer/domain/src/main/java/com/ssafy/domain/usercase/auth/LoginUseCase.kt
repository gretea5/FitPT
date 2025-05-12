package com.ssafy.domain.usercase.auth

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(trainerLoginId: String, trainerPw: String): Flow<ResponseStatus<Unit>> {
        return authRepository.login(
            trainerLoginId = trainerLoginId,
            trainerPw = trainerPw
        )
    }
}