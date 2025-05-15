package com.ssafy.data.network.response.member

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.member.MemberInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberResponse (
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
) : BaseResponse {
    companion object : DataMapper<MemberResponse, MemberInfo> {
        override fun MemberResponse.toDomainModel(): MemberInfo {
            return MemberInfo(
                memberId = this.memberId,
                memberName = this.memberName,
                memberGender = this.memberGender,
                memberBirth = this.memberBirth,
                memberHeight = this.memberHeight,
                memberWeight = this.memberWeight,
                trainerId = this.trainerId,
                trainerName = this.trainerName,
                adminId = this.adminId,
                gymName = this.gymName
            )
        }
    }
}