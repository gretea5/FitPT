package com.sahur.fitpt.domain.member.dto;

import com.sahur.fitpt.db.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDto {
    private Long memberId;
    private Long trainerId;
    private Long adminId;
    private String memberName;
    private String memberGender;
    private LocalDateTime memberBirth;
    private Float memberHeight;
    private Float memberWeight;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .trainerId(member.getTrainer() != null ? member.getTrainer().getTrainerId() : null)
                .adminId(member.getAdmin() != null ? member.getAdmin().getAdminId() : null)
                .memberName(member.getMemberName())
                .memberGender(member.getMemberGender())
                .memberBirth(member.getMemberBirth())
                .memberHeight(member.getMemberHeight())
                .memberWeight(member.getMemberWeight())
                .build();
    }
}