package com.sahur.fitptadmin.domain.member.service;

import com.sahur.fitptadmin.db.entity.Member;
import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.db.repository.MemberRepository;
import com.sahur.fitptadmin.db.repository.TrainerRepository;
import com.sahur.fitptadmin.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public List<MemberDto> getMembers(Long adminId) {

        List<Member> membersWithTrainer = memberRepository.findAllWithTrainerByAdminId(adminId);

        return membersWithTrainer.stream()
                .map(member -> MemberDto.builder()
                        .memberId(member.getMemberId())
                        .memberName(member.getMemberName())
                        .memberGender(member.getMemberGender())
                        .memberBirth(member.getMemberBirth().toLocalDate()) // LocalDateTime → LocalDate 변환
                        .memberHeight(member.getMemberHeight())
                        .memberWeight(member.getMemberWeight())
                        .trainerName(member.getTrainer() != null ? member.getTrainer().getTrainerName() : "없음")
                        .build())
                .toList();
    }

    @Override
    public Long changeTrainer(Long memberId, Long newTrainerId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (newTrainerId != null) {
            Trainer trainer = trainerRepository.findById(newTrainerId)
                    .orElseThrow(() -> new IllegalArgumentException("트레이너가 존재하지 않습니다."));
            member.updateTrainer(trainer);
        } else {
            member.updateTrainer(null); // 트레이너 제거
        }

        return memberId;
    }
}
