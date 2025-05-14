package com.sahur.fitpt.domain.schedule.service;

import com.sahur.fitpt.domain.schedule.dto.ScheduleRequestDto;
import com.sahur.fitpt.domain.schedule.dto.ScheduleResponseDto;

import java.util.List;

public interface ScheduleService {
    Long createSchedule(ScheduleRequestDto requestDto);
    Long updateSchedule(Long scheduleId, ScheduleRequestDto requestDto);
    Long deleteSchedule(Long scheduleId);
    List<ScheduleResponseDto> getSchedulesByDateAndTrainer(int year, int month, int day, Long trainerId);
    List<ScheduleResponseDto> getSchedulesByDateAndMember(int year, int month, int day, Long memberId);
    List<ScheduleResponseDto> getSchedulesByMonthAndMember(int year, int month, Long memberId);
    List<ScheduleResponseDto> getSchedulesByMonthAndTrainer(int year, int month, Long trainerId);
}