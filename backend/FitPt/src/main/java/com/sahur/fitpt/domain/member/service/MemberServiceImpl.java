package com.sahur.fitpt.domain.member.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;
import com.sahur.fitpt.domain.member.dto.MemberRequestDto;
import com.sahur.fitpt.domain.member.dto.MemberResponseDto;
import com.sahur.fitpt.domain.member.dto.MemberPartialUpdateDto;
import com.sahur.fitpt.domain.member.dto.MemberSignUpResponseDto;
import com.sahur.fitpt.domain.member.validator.MemberValidator;
import org.springframework.data.redis.core.RedisTemplate;
import com.sahur.fitpt.db.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;
    private final MemberValidator memberValidator;
    private final FcmTokenRepository fcmTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public MemberSignUpResponseDto createMember(MemberRequestDto requestDto) {
        // 유효성 검사
        memberValidator.validateGender(requestDto.getMemberGender());
        memberValidator.validateHeight(requestDto.getMemberHeight());
        memberValidator.validateWeight(requestDto.getMemberWeight());

        Admin admin = null;

        if (requestDto.getAdminId() != null) {
            admin = adminRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        }

        Member member = Member.builder()
                .admin(admin)
                .memberName(requestDto.getMemberName())
                .memberGender(requestDto.getMemberGender())
                .memberBirth(requestDto.getMemberBirth())
                .memberHeight(requestDto.getMemberHeight())
                .memberWeight(requestDto.getMemberWeight())
                .isDeleted(false)
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("새로운 회원이 등록되었습니다: id={}, name={}", savedMember.getMemberId(), savedMember.getMemberName());
        return MemberSignUpResponseDto.from(savedMember, "dummy_access_token", "dummy_refresh_token");
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberValidator.validateMemberExists(member, memberId);
        return MemberResponseDto.from(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto requestDto) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 유효성 검사
        memberValidator.validateMemberExists(member, memberId);
        memberValidator.validateGender(requestDto.getMemberGender());
        memberValidator.validateHeight(requestDto.getMemberHeight());
        memberValidator.validateWeight(requestDto.getMemberWeight());

        Admin admin = null;

        if (requestDto.getAdminId() != null) {
            admin = adminRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
        }

        member.update(
                requestDto.getMemberName(),
                requestDto.getMemberGender(),
                requestDto.getMemberBirth(),
                requestDto.getMemberHeight(),
                requestDto.getMemberWeight(),
                member.getTrainer(),
                admin
        );

        log.info("회원 정보가 수정되었습니다: id={}, name={}", member.getMemberId(), member.getMemberName());

        return MemberResponseDto.from(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updateMemberPartially(Long memberId, MemberPartialUpdateDto updateDto) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberValidator.validateMemberExists(member, memberId);
        if (updateDto.getMemberWeight() != null) {
            memberValidator.validateWeight(updateDto.getMemberWeight());
        }

        member.updatePartial(updateDto.getMemberName(), updateDto.getMemberWeight());

        log.info("회원 정보가 부분 수정되었습니다: id={}, name={}", member.getMemberId(), member.getMemberName());

        return MemberResponseDto.from(member);
    }

    @Override
    @Transactional
    public Long deleteMember(Long memberId) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberValidator.validateMemberExists(member, memberId);

        try {
            // FCM 토큰 삭제
            List<FcmToken> fcmTokens = fcmTokenRepository.findByMember_MemberId(memberId);
            fcmTokenRepository.deleteAll(fcmTokens);

            // 리프레시 토큰 삭제
            String redisKey = "RT:" + memberId;
            Boolean isDeleted = redisTemplate.delete(redisKey);
            if (Boolean.TRUE.equals(isDeleted)) {
                log.debug("리프레시 토큰 삭제 완료: {}", redisKey);
            }

            // 회원 삭제 처리
            member.delete();

            log.info("회원이 탈퇴처리 되었습니다: id={}", memberId);

            return memberId;
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getMyMembers(Long trainerId) {
        memberValidator.validateTrainerExists(trainerId);

        List<Member> members = memberRepository.findAllByTrainerIdAndNotDeleted(trainerId);
        log.debug("트레이너의 담당 회원 {}명을 조회했습니다: trainerId={}", members.size(), trainerId);

        return members.stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }
}