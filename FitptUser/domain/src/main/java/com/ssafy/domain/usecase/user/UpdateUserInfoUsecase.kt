package com.ssafy.domain.usecase.user

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.user.DataStoreRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateUserInfoUsecase @Inject constructor(private val userRepository: UserRepository, private val dataStoreRepository: DataStoreRepository){
    suspend operator fun invoke(userInfo: UserInfo): Flow<ResponseStatus<Unit>> {
        return userRepository.updateUserInfo(userInfo)
    }
}
