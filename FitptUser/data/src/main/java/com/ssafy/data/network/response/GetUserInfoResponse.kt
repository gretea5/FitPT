package com.ssafy.data.network.response

data class GetUserInfoResponse(
    val adminId: Int,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: Int,
    val memberId: Int,
    val memberName: String,
    val memberWeight: Int,
    val trainerId: Any
)