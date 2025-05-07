package com.sahur.fitpt.domain.report.controller;


import com.sahur.fitpt.domain.report.dto.ReportDetailResponseDto;
import com.sahur.fitpt.domain.report.dto.ReportRequestDto;
import com.sahur.fitpt.domain.report.dto.ReportResponseDto;
import com.sahur.fitpt.domain.report.service.ReportService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Long> createReport(@RequestBody ReportRequestDto requestDto) {
        return new ResponseEntity<>(reportService.createReport(requestDto), HttpStatus.CREATED);
    }

    @Parameters({
            @Parameter(name = "reportId", description = "보고서 Id", example = "1"),
    })
    @PatchMapping("/{reportId}")
    public ResponseEntity<Long> updateReport(
            @PathVariable Long reportId,
            @RequestBody ReportRequestDto requestDto) {
        return new ResponseEntity<>(reportService.updateReport(reportId, requestDto), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "memberId", description = "회원 Id", example = "1"),
    })
    @GetMapping
    public ResponseEntity<List<ReportResponseDto>> getAllReports(@RequestParam(value = "memberId") Long memberId) {
        List<ReportResponseDto> reports = reportService.getAllReports(memberId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponseDto> getReport(@PathVariable Long reportId) {
        return new ResponseEntity<>(reportService.getReport(reportId), HttpStatus.OK);
    }
}
