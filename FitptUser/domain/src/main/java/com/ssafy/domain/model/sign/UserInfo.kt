package com.ssafy.domain.model.sign

data class UserInfo(
    val admin: Int,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: Double,
    val memberName: String,
    val memberWeight: Double
)