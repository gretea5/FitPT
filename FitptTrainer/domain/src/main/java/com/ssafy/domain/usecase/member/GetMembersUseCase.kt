package com.ssafy.domain.usecase.member

import com.ssafy.domain.repository.member.MemberRepository
import javax.inject.Inject

class GetMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() = memberRepository.getMembers()
}