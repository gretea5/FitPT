package com.ssafy.data.network.response.trainer

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.auth.TrainerLogin
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainerLoginResponse(
    val trainerId: Int,
    val trainerName: String,
    val accessToken: String,
    val refreshToken: String
) : BaseResponse {
    companion object : DataMapper<TrainerLoginResponse, TrainerLogin> {
        override fun TrainerLoginResponse.toDomainModel(): TrainerLogin {
            return TrainerLogin(
                trainerId = this.trainerId,
                trainerName = this.trainerName,
                accessToken = this.accessToken,
                refreshToken = this.refreshToken
            )
        }
    }
}