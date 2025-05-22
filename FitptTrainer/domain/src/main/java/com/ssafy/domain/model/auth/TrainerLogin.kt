package com.ssafy.domain.model.auth

import com.ssafy.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainerLogin (
    val trainerId: Int,
    val trainerName: String,
    val accessToken: String,
    val refreshToken: String
) : BaseModel