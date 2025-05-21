package com.ssafy.data.network.request

data class UserRequest(
    val adminId: Long,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: Double,
    val memberId: Long,
    val memberName: String,
    val memberWeight: Double,
    val trainerId: Long
)