package com.ssafy.data.network.request

data class UserLoginRequest(
    val kakaoAccessToken: String,
    val fcmToken: String
)