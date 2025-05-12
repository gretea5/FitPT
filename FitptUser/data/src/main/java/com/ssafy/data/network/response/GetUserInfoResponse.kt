package com.ssafy.data.network.response

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.login.UserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUserInfoResponse(
    val adminId: Int,
    val memberBirth: String,
    val memberGender: String,
    val memberHeight: Int,
    val memberId: Int,
    val memberName: String,
    val memberWeight: Int,
    val trainerId: Int,
) :BaseResponse{
    companion object : DataMapper<GetUserInfoResponse, UserInfo> {
        override fun GetUserInfoResponse.toDomainModel(): UserInfo {
            return UserInfo(
                admin = this.adminId,
                memberName = this.memberName,
                memberGender = this.memberGender,
                memberHeight = this.memberHeight.toDouble(),
                memberWeight = this.memberWeight.toDouble(),
                memberBirth = this.memberBirth,
                trainerId = this.trainerId
            )
        }
    }
}