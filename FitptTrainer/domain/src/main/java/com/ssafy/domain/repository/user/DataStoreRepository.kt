package com.ssafy.domain.repository.user

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val kakaoAccessToken: Flow<String?>
    suspend fun saveKakaoAccessToken(token: String)

    val jwtToken: Flow<String?>
    suspend fun saveJwtToken(token: String)

    val nickname: Flow<String?>
    suspend fun saveNickname(nickname: String)

    val trainerId: Flow<Long?>
    suspend fun saveTrainerId(userId: Long)

    val fcmToken: Flow<String?>
    suspend fun saveFcmToken(fcmToken: String)

    suspend fun clearAll()
}