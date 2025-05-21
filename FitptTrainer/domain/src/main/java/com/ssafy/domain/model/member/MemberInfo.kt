package com.ssafy.domain.model.member

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberInfo(
    val memberId: Long,
    val memberName: String,
    val memberGender: String,
    val memberBirth: String,
    val memberHeight: Int,
    val memberWeight: Int,
    val trainerId: Long,
    val trainerName: String,
    val adminId: Long,
    val gymName: String
) : BaseModel
