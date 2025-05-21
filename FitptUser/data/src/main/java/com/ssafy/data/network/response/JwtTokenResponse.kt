package com.ssafy.data.network.response

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.auth.JwtToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class JwtTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val memberId: Int,
    val memberName: String,
): BaseResponse {
    companion object: DataMapper<JwtTokenResponse, JwtToken> {
        override fun JwtTokenResponse.toDomainModel(): JwtToken {
            return JwtToken(
                accessToken = this.accessToken,
                memberId = this.memberId
            )
        }
    }
}