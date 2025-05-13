package com.ssafy.domain.model.login

import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    val memberId: Int = 0 ,
    val admin: Int = 0,
    val memberName: String = "",
    val memberGender: String = "",
    val memberHeight: Double = 0.0,
    val memberWeight: Double = 0.0,
    val memberBirth: String = "",
    val trainerId: Int=0
) : BaseModel