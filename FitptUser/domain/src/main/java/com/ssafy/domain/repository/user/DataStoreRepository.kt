package com.ssafy.domain.repository.user

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val kakaoAccessToken: Flow<String?>
    suspend fun saveKakaoAccessToken(token: String)

    val jwtToken: Flow<String?>
    suspend fun saveJwtToken(token: String)

    val nickname: Flow<String?>
    suspend fun saveNickname(nickname: String)

    val userId: Flow<Long?>
    suspend fun saveUserId(userId: Long)

    val fcmToken: Flow<String?>
    suspend fun saveFcmToken(fcmToken: String)

    suspend fun clearAll()
}