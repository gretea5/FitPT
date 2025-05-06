package com.sahur.fitpt.domain.schedule.controller;

import com.sahur.fitpt.domain.schedule.dto.ScheduleRequestDto;
import com.sahur.fitpt.domain.schedule.dto.ScheduleResponseDto;
import com.sahur.fitpt.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "PT 일정 관리 API")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "PT 일정 생성", description = "새로운 PT 일정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 생성 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content
            )
    })
    public ResponseEntity<Long> createSchedule(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "일정 생성 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleRequestDto.class))
            ) ScheduleRequestDto requestDto) {
        Long scheduleId = scheduleService.createSchedule(requestDto);
        return ResponseEntity.ok(scheduleId);
    }

    @GetMapping
    @Operation(summary = "PT 일정 조회", description = "날짜/월별로 트레이너 또는 회원의 PT 일정을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 조회 성공",
                    content = @Content(schema = @Schema(implementation = ScheduleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content
            )
    })
    public ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)")
            @RequestParam(required = false) String date,

            @Parameter(description = "조회할 월 (YYYY-MM 형식)")
            @RequestParam(required = false) String month,

            @Parameter(description = "트레이너 ID")
            @RequestParam(required = false) Long trainerId,

            @Parameter(description = "회원 ID")
            @RequestParam(required = false) Long memberId) {

        try {
            validateParameters(date, month, trainerId, memberId);

            if (date != null) {
                return handleDateSearch(date, trainerId, memberId);
            } else if (month != null) {
                return handleMonthSearch(month, trainerId, memberId);
            }

            log.warn("Neither date nor month provided");
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void validateParameters(String date, String month, Long trainerId, Long memberId) {
        if (trainerId == null && memberId == null) {
            log.warn("Neither trainerId nor memberId provided");
            throw new IllegalArgumentException("Either trainerId or memberId must be provided");
        }
    }

    private ResponseEntity<List<ScheduleResponseDto>> handleDateSearch(String date, Long trainerId, Long memberId) {
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD");
        }

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        log.debug("Parsed date: year={}, month={}, day={}", year, month, day);

        List<ScheduleResponseDto> result;
        if (trainerId != null) {
            result = scheduleService.getSchedulesByDateAndTrainer(year, month, day, trainerId);
            log.debug("Found {} schedules for trainer {} on date {}", result.size(), trainerId, date);
        } else {
            result = scheduleService.getSchedulesByDateAndMember(year, month, day, memberId);
            log.debug("Found {} schedules for member {} on date {}", result.size(), memberId, date);
        }

        return ResponseEntity.ok(result);
    }

    private ResponseEntity<List<ScheduleResponseDto>> handleMonthSearch(String month, Long trainerId, Long memberId) {
        String[] monthParts = month.split("-");
        if (monthParts.length != 2) {
            throw new IllegalArgumentException("Invalid month format. Expected YYYY-MM");
        }

        int year = Integer.parseInt(monthParts[0]);
        int monthValue = Integer.parseInt(monthParts[1]);

        log.debug("Parsed month: year={}, month={}", year, monthValue);

        List<ScheduleResponseDto> result;
        if (trainerId != null) {
            result = scheduleService.getSchedulesByMonthAndTrainer(year, monthValue, trainerId);
            log.debug("Found {} schedules for trainer {} in month {}", result.size(), trainerId, month);
        } else {
            result = scheduleService.getSchedulesByMonthAndMember(year, monthValue, memberId);
            log.debug("Found {} schedules for member {} in month {}", result.size(), memberId, month);
        }

        return ResponseEntity.ok(result);
    }

    private ResponseEntity<List<ScheduleResponseDto>> handleException(Exception e) {
        if (e instanceof NumberFormatException) {
            log.error("Failed to parse date/month values: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } else if (e instanceof IllegalArgumentException) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } else {
            log.error("Unexpected error during schedule search: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}