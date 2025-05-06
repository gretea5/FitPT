package com.sahur.fitpt.domain.composition.service;

import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.CompositionLog;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.CompositionRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompositionServiceImpl implements CompositionService {
    private final CompositionRepository compositionRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long saveComposition(CompositionRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND));

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

    @Override
    public Optional<CompositionResponseDto> getCompositionById(Long compositionLogId) {
        return compositionRepository.findById(compositionLogId)
                .map(CompositionResponseDto::fromEntity);
    }

    @Override
    public List<CompositionResponseDto> getCompositionsByMemberId(Long memberId) {
        return compositionRepository.findAllByMemberMemberId(memberId)
                .stream()
                .map(CompositionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CompositionResponseDto> getCompositionsByUserIdWithSort(Long memberId, String sortField, String order) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);
        return compositionRepository.findAllByMemberMemberId(memberId, sort)
                .stream()
                .map(CompositionResponseDto::fromEntity)
                .toList();
    }
}