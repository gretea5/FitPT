package com.ssafy.domain.usecase.member

import com.ssafy.domain.repository.member.MemberRepository
import javax.inject.Inject

class GetMemberInfoByIdUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(memberId: Long) = memberRepository.getMemberInfoById(memberId)
}