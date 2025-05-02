package com.sahur.fitptadmin.domain.trainer.service;

import com.sahur.fitptadmin.db.entity.Admin;
import com.sahur.fitptadmin.db.entity.Member;
import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.db.repository.AdminRepository;
import com.sahur.fitptadmin.db.repository.MemberRepository;
import com.sahur.fitptadmin.db.repository.TrainerRepository;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerRegisterDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<TrainerResponseDto> getTrainers(Long adminId) {
        List<Trainer> trainers = trainerRepository.findByAdmin_AdminId(adminId);

        return trainers.stream()
                .map(trainer -> TrainerResponseDto.builder()
                        .trainerId(trainer.getTrainerId())
                        .trainerLoginId(trainer.getTrainerLoginId())
                        .trainerPw(trainer.getTrainerPw())
                        .trainerName(trainer.getTrainerName())
                        .build())
                .toList();
    }

    @Override
    public Long registerTrainer(TrainerRegisterDto trainerRegisterDto) {

        // 트레이너 ID 중복 검사
        if (trainerRepository.existsByTrainerLoginId(trainerRegisterDto.getTrainerLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

        // 관리자 조회
        Admin admin = adminRepository.findById(trainerRegisterDto.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        Trainer trainer = Trainer.builder()
                .trainerLoginId(trainerRegisterDto.getTrainerLoginId())
                .trainerPw(trainerRegisterDto.getTrainerPw())
                .trainerName(trainerRegisterDto.getTrainerName())
                .admin(admin)
                .build();

        trainerRepository.save(trainer);

        return trainer.getTrainerId();
    }

    @Override
    public Long updateTrainerInfo(Long trainerId, TrainerUpdateRequestDto trainerUpdateRequestDto) {

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));

        trainer.updateTrainerInfo(trainerUpdateRequestDto.getTrainerName(), trainerUpdateRequestDto.getTrainerLoginId(), trainerUpdateRequestDto.getTrainerPw());

        return trainer.getTrainerId();
    }

    @Override
    public Long deleteTrainer(Long trainerId) {

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));

        List<Member> members = memberRepository.findByTrainerWithFetch(trainer);

        for (Member member : members) {
            member.updateTrainer(null);
        }

        memberRepository.saveAll(members);

        Long deletedTrainerId = trainer.getTrainerId();
        trainerRepository.deleteById(deletedTrainerId);

        return deletedTrainerId;
    }
}
