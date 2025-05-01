package com.ssafy.fitptuser

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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