package com.sahur.fitpt.domain.member.service;

import com.sahur.fitpt.domain.member.dto.MemberRequestDto;
import com.sahur.fitpt.domain.member.dto.MemberResponseDto;
import com.sahur.fitpt.domain.member.dto.MemberPartialUpdateDto;
import com.sahur.fitpt.domain.member.dto.MemberSignUpResponseDto;

import java.util.List;

public interface MemberService {
    // 회원 등록
    MemberSignUpResponseDto createMember(MemberRequestDto requestDto);

    // 회원 정보 조회
    MemberResponseDto getMember(Long memberId);

    // 회원 정보 전체 수정
    MemberResponseDto updateMember(Long memberId, MemberRequestDto requestDto);

    // 회원 정보 부분 수정
    MemberResponseDto updateMemberPartially(Long memberId, MemberPartialUpdateDto updateDto);

    // 회원 탈퇴 (논리적 삭제)
    Long deleteMember(Long memberId);

    List<MemberResponseDto> getMyMembers(Long trainerId);
}