package com.ssafy.fitpttrainer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "ApplicationClass"
@HiltAndroidApp
class ApplicationClass : Application() {
    //@Inject
    //lateinit var userDataStoreSource: UserDataStoreSource

    override fun onCreate() {
        super.onCreate()
        //KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)
        /*FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    CoroutineScope(Dispatchers.IO).launch {
                        //userDataStoreSource.saveFcmToken(token)
                    }
                } else {
                }
            }*/
    }
}