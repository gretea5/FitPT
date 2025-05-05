package com.sahur.fitpt.domain.report.controller;


import com.sahur.fitpt.domain.report.dto.ReportRequestDto;
import com.sahur.fitpt.domain.report.service.ReportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Long> createReport(@RequestBody ReportRequestDto requestDto) {
        Long reportId = reportService.createReport(requestDto);

        return new ResponseEntity<>(reportId, HttpStatus.CREATED);
    }
}
