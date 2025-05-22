package com.ssafy.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrainerDataStoreSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KAKAO_ACCESS_TOKEN = stringPreferencesKey("kakao_access_token")
        val NICKNAME = stringPreferencesKey("nickname")
        val TRAINER_ID = longPreferencesKey("trainer_id")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val JWT_TOKEN =  stringPreferencesKey("jwt_token")
        val TRAINER_OBJECT = stringPreferencesKey("trainer_object")
    }

    private val gson = Gson()

    suspend fun saveKakaoAccessToken(kakaoAccessToken: String) {
        dataStore.edit { preferences ->
            preferences[KAKAO_ACCESS_TOKEN] = kakaoAccessToken
        }
    }

    suspend fun saveFcmToken(fcmToken: String){
        dataStore.edit{preferences ->
            preferences[FCM_TOKEN] = fcmToken
        }
    }

    suspend fun saveNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME] = nickname
        }
    }

    suspend fun saveTrainerId(trainerId: Long) {
        dataStore.edit { preferences ->
            preferences[TRAINER_ID] = trainerId
        }
    }

    suspend fun saveJwtToken(jwtToken: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = jwtToken
        }
    }

    /*suspend fun saveUser(user: UserInfo) {
        val userJson = gson.toJson(user)
        dataStore.edit { preferences ->
            preferences[USER_OBJECT] = userJson
        }
    }*/

    val kakaoAccessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KAKAO_ACCESS_TOKEN]
    }

    val nickname: Flow<String?> = dataStore.data.map { preferences ->
        preferences[NICKNAME]
    }

    val trainerId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[TRAINER_ID]
    }

    val fcmToken : Flow<String?> = dataStore.data.map{preferences->
        preferences[FCM_TOKEN]
    }

    val jwtToken : Flow<String?> = dataStore.data.map{preferences->
        preferences[JWT_TOKEN]
    }

    /*val user: Flow<UserInfo?> = dataStore.data.map { preferences ->
        val userJson = preferences[USER_OBJECT]
        userJson?.let { gson.fromJson(it, UserInfo::class.java) }
    }*/

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}