package com.sahur.fitptadmin.domain.member.service;

import com.sahur.fitptadmin.domain.member.dto.MemberDto;

import java.util.List;

public interface MemberService {
    List<MemberDto> getMembers(Long adminId);
}
