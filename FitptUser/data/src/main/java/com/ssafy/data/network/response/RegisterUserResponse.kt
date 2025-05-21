package com.ssafy.data.network.response

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.auth.JwtToken
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterUserResponse(
    val accessToken: String,
    val adminId: String,
    val fcmToken: String,
    val gymName: String,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: String,
    val memberId: Int,
    val memberName: String,
    val memberWeight: String,
    val refreshToken: String
): BaseResponse {
    companion object: DataMapper<RegisterUserResponse, JwtToken> {
        override fun RegisterUserResponse.toDomainModel(): JwtToken {
            return JwtToken(
                accessToken = this.accessToken,
                memberId = this.memberId
            )
        }
    }
}