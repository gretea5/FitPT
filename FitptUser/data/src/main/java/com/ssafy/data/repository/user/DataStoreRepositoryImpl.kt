package com.ssafy.data.repository.user

import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.user.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStoreSource
): DataStoreRepository {
    override val kakaoAccessToken: Flow<String?> = dataStore.kakaoAccessToken

    override suspend fun saveKakaoAccessToken(token: String) = dataStore.saveKakaoAccessToken(token)

    override val jwtToken: Flow<String?> = dataStore.jwtToken

    override suspend fun saveJwtToken(token: String) = dataStore.saveJwtToken(token)

    override val nickname: Flow<String?> = dataStore.nickname

    override suspend fun saveNickname(nickname: String) = dataStore.saveNickname(nickname)

    override val userId: Flow<Long?> = dataStore.userId

    override suspend fun saveUserId(userId: Long) = dataStore.saveUserId(userId)

    override val fcmToken: Flow<String?> = dataStore.fcmToken

    override suspend fun saveFcmToken(fcmToken: String) = dataStore.saveFcmToken(fcmToken)

    override val user: Flow<UserInfo?> = dataStore.user
    override suspend fun saveUser(user: UserInfo) = dataStore.saveUser(user)

    override suspend fun clearAll() = dataStore.clearAll()
}