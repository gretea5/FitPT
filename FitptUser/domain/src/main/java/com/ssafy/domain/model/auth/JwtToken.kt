package com.ssafy.domain.model.auth

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class JwtToken(
    val accessToken: String,
    val memberId: Int
): BaseModel
