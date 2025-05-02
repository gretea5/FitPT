package com.sahur.fitpt.domain.composition.controller;

import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import com.sahur.fitpt.domain.composition.service.CompositionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/body-composition")
@RequiredArgsConstructor
public class CompositionController {
    private final CompositionService compositionService;

    @GetMapping
    public ResponseEntity<List<CompositionResponseDto>> getCompositions(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "order", required = false) String order
    ) {
        if (memberId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (sort == null && order == null) {
            List<CompositionResponseDto> result = compositionService.getCompositionsByMemberId(memberId);
            return ResponseEntity.ok(result);
        }

        if (sort != null && order != null) {
            List<CompositionResponseDto> result = compositionService.getCompositionsByUserIdWithSort(memberId, sort, order);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{compositionLogId}")
    public ResponseEntity<CompositionResponseDto> getCompositionById(@PathVariable Long compositionLogId) {
        return compositionService.getCompositionById(compositionLogId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createCompositionLog(@RequestBody CompositionRequestDto request) {
        Long savedId = compositionService.saveComposition(request);
        return new ResponseEntity<>(savedId, HttpStatus.CREATED);
    }
}