package com.ssafy.fitptuser

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.data.datasource.UserDataStoreSource
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ApplicationClass"
@HiltAndroidApp
class ApplicationClass : Application() {
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onCreate() {
        super.onCreate()
        //KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)
        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG,"파이어베이스 토큰"+token)
                    CoroutineScope(Dispatchers.IO).launch {
                        userDataStoreSource.saveFcmToken(token)
                    }
                } else {
                }
            }
    }
}