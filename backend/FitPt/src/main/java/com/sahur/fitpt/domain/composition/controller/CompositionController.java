package com.sahur.fitpt.domain.composition.controller;

import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.service.CompositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/body-composition")
@RequiredArgsConstructor
public class CompositionController {
    private final CompositionService compositionService;

    @PostMapping
    public ResponseEntity<Long> createCompositionLog(@RequestBody CompositionRequestDto request) {
        Long savedId = compositionService.saveComposition(request);
        return new ResponseEntity<>(savedId, HttpStatus.CREATED);
    }
}