package com.sahur.fitpt.domain.member.service;

import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.Trainer;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.db.repository.TrainerRepository;
import com.sahur.fitpt.domain.member.dto.MemberRequestDto;
import com.sahur.fitpt.domain.member.dto.MemberResponseDto;
import com.sahur.fitpt.domain.member.dto.MemberPartialUpdateDto;
import com.sahur.fitpt.domain.member.validator.MemberValidator;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    @Transactional
    public Long createMember(MemberRequestDto requestDto) {
        // 유효성 검사
        memberValidator.validateGender(requestDto.getMemberGender());
        memberValidator.validateHeight(requestDto.getMemberHeight());
        memberValidator.validateWeight(requestDto.getMemberWeight());

        Trainer trainer = null;
        Admin admin = null;

        if (requestDto.getTrainerId() != null) {
            trainer = trainerRepository.findById(requestDto.getTrainerId())
                    .orElseThrow(() -> new EntityNotFoundException("ID가 " + requestDto.getTrainerId() + "인 트레이너를 찾을 수 없습니다"));
        }

        if (requestDto.getAdminId() != null) {
            admin = adminRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new EntityNotFoundException("ID가 " + requestDto.getAdminId() + "인 체육관을 찾을 수 없습니다"));
        }

        Member member = Member.builder()
                .trainer(trainer)
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

        return savedMember.getMemberId();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다"));

        memberValidator.validateMemberExists(member, memberId);
        return MemberResponseDto.from(member);
    }

    @Override
    @Transactional
    public Long updateMember(Long memberId, MemberRequestDto requestDto) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다"));

        // 유효성 검사
        memberValidator.validateMemberExists(member, memberId);
        memberValidator.validateGender(requestDto.getMemberGender());
        memberValidator.validateHeight(requestDto.getMemberHeight());
        memberValidator.validateWeight(requestDto.getMemberWeight());

        Trainer trainer = null;
        Admin admin = null;

        if (requestDto.getTrainerId() != null) {
            trainer = trainerRepository.findById(requestDto.getTrainerId())
                    .orElseThrow(() -> new EntityNotFoundException("ID가 " + requestDto.getTrainerId() + "인 트레이너를 찾을 수 없습니다"));
        }

        if (requestDto.getAdminId() != null) {
            admin = adminRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new EntityNotFoundException("ID가 " + requestDto.getAdminId() + "인 체육관을 찾을 수 없습니다"));
        }

        member.update(
                requestDto.getMemberName(),
                requestDto.getMemberGender(),
                requestDto.getMemberBirth(),
                requestDto.getMemberHeight(),
                requestDto.getMemberWeight(),
                trainer,
                admin
        );

        log.info("회원 정보가 수정되었습니다: id={}, name={}", member.getMemberId(), member.getMemberName());

        return member.getMemberId();
    }

    @Override
    @Transactional
    public MemberResponseDto updateMemberPartially(Long memberId, MemberPartialUpdateDto updateDto) {
        Member member = memberRepository.findByIdAndNotDeleted(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다"));

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
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다"));

        memberValidator.validateMemberExists(member, memberId);

        member.delete();
        log.info("회원이 삭제되었습니다: id={}", memberId);

        return memberId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getMembersByTrainer(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new EntityNotFoundException("ID가 " + trainerId + "인 트레이너를 찾을 수 없습니다");
        }

        List<Member> members = memberRepository.findAllByTrainerIdAndNotDeleted(trainerId);
        log.debug("트레이너의 담당 회원 {}명을 조회했습니다: trainerId={}", members.size(), trainerId);

        return members.stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }
}