package com.sahur.fitpt.domain.report.service;

import com.sahur.fitpt.domain.report.dto.ReportDetailResponseDto;
import com.sahur.fitpt.domain.report.dto.ReportRequestDto;
import com.sahur.fitpt.domain.report.dto.ReportResponseDto;

import java.util.List;

public interface ReportService {
    Long createReport(ReportRequestDto requestDto);

    Long updateReport(Long reportId, ReportRequestDto requestDto);

    List<ReportResponseDto> getAllReports(Long memberId);

    ReportDetailResponseDto getReport(Long reportId);
}