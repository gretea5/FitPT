package com.ssafy.data.network.request

data class RegisterUserInfoRequest(
    val adminId: Int,
    val fcmToken: String,
    val kakaoAccessToken: String,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: Double,
    val memberName: String,
    val memberWeight: Double
)