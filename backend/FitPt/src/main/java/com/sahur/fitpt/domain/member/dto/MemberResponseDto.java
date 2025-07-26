package com.sahur.fitpt.domain.member.dto;

import com.sahur.fitpt.db.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String memberName;
    private String memberGender;
    private LocalDate memberBirth;
    private Float memberHeight;
    private Float memberWeight;
    private Long trainerId;
    private String trainerName;
    private Long adminId;
    private String gymName;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberGender(member.getMemberGender())
                .memberBirth(member.getMemberBirth())
                .memberHeight(member.getMemberHeight())
                .memberWeight(member.getMemberWeight())
                .trainerId(member.getTrainer() != null ? member.getTrainer().getTrainerId() : null)
                .trainerName(member.getTrainer() != null ? member.getTrainer().getTrainerName() : null)
                .adminId(member.getAdmin() != null ? member.getAdmin().getAdminId() : null)
                .gymName(member.getAdmin() != null ? member.getAdmin().getGymName() : null)
                .build();
    }

}