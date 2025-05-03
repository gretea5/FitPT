package com.sahur.fitpt.domain.composition.controller;

import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import com.sahur.fitpt.domain.composition.service.CompositionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/body-composition")
@RequiredArgsConstructor
@Tag(name = "Body Composition", description = "체성분 API")
public class CompositionController {
    private final CompositionServiceImpl compositionService;

    @GetMapping
    @Operation(summary = "내 체성분 조회", description = "회원의 체성분을 조회하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원 Id", example = "1"),
            @Parameter(name = "sort", description = "createdAt", example = "createdAt"),
            @Parameter(name = "order", description = "asc 또는 desc", example = "desc"),
    })
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
    @Operation(summary = "내 체성분 상세 조회", description = "회원의 체성분을 상세 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    @Parameters({
            @Parameter(name = "compositionLogId", description = "체성분 Id", example = "1"),
    })
    public ResponseEntity<CompositionResponseDto> getCompositionById(@PathVariable Long compositionLogId) {
        return compositionService.getCompositionById(compositionLogId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "내 체성분 상세 조회", description = "회원의 체성분을 상세 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Long> createCompositionLog(@RequestBody CompositionRequestDto request) {
        Long savedId = compositionService.saveComposition(request);
        return new ResponseEntity<>(savedId, HttpStatus.CREATED);
    }
}