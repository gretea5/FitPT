package com.sahur.fitpt.domain.composition.service;

import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;

import java.util.List;
import java.util.Optional;

public interface CompositionService {
    Long saveComposition(CompositionRequestDto request);

    Optional<CompositionResponseDto> getCompositionById(Long compositionLogId);

    List<CompositionResponseDto> getCompositionsByMemberId(Long memberId);

    List<CompositionResponseDto> getCompositionsByUserIdWithSort(Long memberId, String sortField, String order);
}
