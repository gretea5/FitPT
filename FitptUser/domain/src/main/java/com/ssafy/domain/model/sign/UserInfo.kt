package com.ssafy.domain.model.sign

data class UserInfo(
    val admin: Int = 0,
    val memberName: String = "",
    val memberGender: String = "",
    val memberHeight: Double = 0.0,
    val memberWeight: Double = 0.0,
    val memberBirth: String = ""
)