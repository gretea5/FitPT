package com.sahur.fitpt.domain.composition.service;

import com.sahur.fitpt.db.entity.CompositionLog;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.CompositionRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompositionService {
    private final CompositionRepository compositionRepository;
    private final MemberRepository memberRepository;

    public Long saveComposition(CompositionRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        CompositionLog compositionLog = CompositionLog.builder()
                .member(member)
                .protein(request.getProtein())
                .bmr(request.getBmr())
                .mineral(request.getMineral())
                .bodyAge(request.getBodyAge())
                .smm(request.getSmm())
                .icw(request.getIcw())
                .ecw(request.getEcw())
                .bfm(request.getBfm())
                .bfp(request.getBfp())
                .weight(request.getWeight())
                .build();

        CompositionLog savedCompositionLog = compositionRepository.save(compositionLog);

        return savedCompositionLog.getCompositionLogId();
    }
}