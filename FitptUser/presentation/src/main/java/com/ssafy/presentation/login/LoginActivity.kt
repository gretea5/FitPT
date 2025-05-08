package com.ssafy.presentation.login

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.presentation.PermissionChecker
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ActivityLoginBinding
import com.ssafy.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }

    private val checker = PermissionChecker(this)
    private val runtimePermissions =
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private fun checkPermission() {
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.setOnGrantedListener {
                initNotification()
            }
            checker.requestPermissionLauncher.launch(runtimePermissions)
        } else {
            initNotification()
        }
    }

    private fun initNotification() {
        createNotificationChannel(channel_id, "fitptuser")
    }

    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
        }
    }

    companion object {
        const val channel_id = "fitptuser_channel"
    }
}