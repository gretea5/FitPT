package com.sahur.fitpt.domain.report.controller;


import com.sahur.fitpt.domain.report.dto.ReportDetailResponseDto;
import com.sahur.fitpt.domain.report.dto.ReportRequestDto;
import com.sahur.fitpt.domain.report.dto.ReportResponseDto;
import com.sahur.fitpt.domain.report.service.ReportService;

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
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "보고서 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "보고서 작성", description = "트레이너 PT의 보고서를 작성하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Long> createReport(@RequestBody ReportRequestDto requestDto) {
        return new ResponseEntity<>(reportService.createReport(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{reportId}")
    @Operation(summary = "보고서 수정", description = "보고서 일부 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    @Parameters({
            @Parameter(name = "reportId", description = "보고서 Id", example = "1")
    })
    public ResponseEntity<Long> updateReport(
            @PathVariable Long reportId,
            @RequestBody ReportRequestDto requestDto) {
        return new ResponseEntity<>(reportService.updateReport(reportId, requestDto), HttpStatus.OK);
    }

    @Operation(summary = "보고서 전체 조회", description = "회원의 모든 보고서를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원 Id", example = "1"),
    })
    @GetMapping
    public ResponseEntity<List<ReportResponseDto>> getAllReports(@RequestParam(value = "memberId") Long memberId) {
        return new ResponseEntity<>(reportService.getAllReports(memberId), HttpStatus.OK);
    }


    @Operation(summary = "보고서 상세 조회", description = "보고서를 상세 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponseDto> getReport(@PathVariable Long reportId) {
        return new ResponseEntity<>(reportService.getReport(reportId), HttpStatus.OK);
    }
}
