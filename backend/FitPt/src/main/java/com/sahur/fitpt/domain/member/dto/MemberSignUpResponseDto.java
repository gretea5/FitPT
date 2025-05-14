package com.sahur.fitpt.domain.member.dto;

import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.FcmToken;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemberSignUpResponseDto {
    private Long memberId;
    private String memberName;
    private String memberGender;
    private LocalDate memberBirth;
    private Float memberHeight;
    private Float memberWeight;
    private Long adminId;
    private String gymName;
    private String accessToken;
    private String refreshToken;
    private List<String> fcmTokens;

    public static MemberSignUpResponseDto from(Member member, String accessToken, String refreshToken) {
        return MemberSignUpResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberGender(member.getMemberGender())
                .memberBirth(member.getMemberBirth())
                .memberHeight(member.getMemberHeight())
                .memberWeight(member.getMemberWeight())
                .adminId(member.getAdmin() != null ? member.getAdmin().getAdminId() : null)
                .gymName(member.getAdmin() != null ? member.getAdmin().getGymName() : null)
                .fcmTokens(member.getFcmTokens().stream()
                        .map(FcmToken::getToken)
                        .collect(Collectors.toList()))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}