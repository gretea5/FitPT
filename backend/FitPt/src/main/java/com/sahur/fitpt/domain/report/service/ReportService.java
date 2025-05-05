package com.sahur.fitpt.domain.report.service;

import com.sahur.fitpt.domain.report.dto.ReportRequestDto;

public interface ReportService {
    Long createReport(ReportRequestDto requestDto);
    Long updateReport(Long reportId, ReportRequestDto requestDto);
}