package com.sahur.fitpt.domain.schedule.controller;

import com.sahur.fitpt.core.auth.annotation.TrainerMemberAccess;
import com.sahur.fitpt.core.auth.annotation.TrainerMemberAccess.Domain;
import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @TrainerMemberAccess(domain = Domain.SCHEDULE, accessType = TrainerMemberAccess.AccessType.TRAINER_ONLY)
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
    public ResponseEntity<Long> createSchedule(@RequestBody ScheduleRequestDto requestDto) {
        try {
            log.debug("일정 생성 컨트롤러 시작 - requestDto: {}", requestDto);

            // 기본 유효성 검사
            if (requestDto.getMemberId() == null || requestDto.getTrainerId() == null ||
                    requestDto.getStartTime() == null || requestDto.getEndTime() == null) {
                log.error("필수 입력값 누락 - requestDto: {}", requestDto);
                throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
            }

            Long scheduleId = scheduleService.createSchedule(requestDto);
            log.debug("일정 생성 완료 - scheduleId: {}", scheduleId);

            return ResponseEntity.ok(scheduleId);
        } catch (Exception e) {
            log.error("일정 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }



    @PutMapping("/{scheduleId}")
    @TrainerMemberAccess(domain = Domain.SCHEDULE, accessType = TrainerMemberAccess.AccessType.TRAINER_ONLY)
    @Operation(summary = "PT 일정 수정", description = "기존 PT 일정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 수정 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정을 찾을 수 없음",
                    content = @Content
            )
    })
    public ResponseEntity<Long> updateSchedule(
            @Parameter(description = "수정할 일정 ID", required = true)
            @PathVariable Long scheduleId,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "일정 수정 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleRequestDto.class))
            ) ScheduleRequestDto requestDto) {
        Long updatedScheduleId = scheduleService.updateSchedule(scheduleId, requestDto);
        return ResponseEntity.ok(updatedScheduleId);
    }

    @DeleteMapping("/{scheduleId}")
    @TrainerMemberAccess(domain = Domain.SCHEDULE, accessType = TrainerMemberAccess.AccessType.TRAINER_ONLY)
    @Operation(summary = "PT 일정 삭제", description = "PT 일정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 삭제 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정을 찾을 수 없음",
                    content = @Content
            )
    })
    public ResponseEntity<Long> deleteSchedule(
            @Parameter(description = "삭제할 일정 ID", required = true)
            @PathVariable Long scheduleId) {
        Long deletedScheduleId = scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(deletedScheduleId);
    }

    @GetMapping
    @TrainerMemberAccess(domain = Domain.SCHEDULE, accessType = TrainerMemberAccess.AccessType.BOTH_ALLOWED)
    @Operation(summary = "PT 일정 조회", description = "날짜/월별로 PT 일정을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음")
    })
    public ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)")
            @RequestParam(required = false) String date,
            @Parameter(description = "조회할 월 (YYYY-MM 형식)")
            @RequestParam(required = false) String month) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Long authenticatedId = Long.parseLong(authentication.getName());

        log.debug("인증된 사용자 - ID: {}, Role: {}", authenticatedId, role);

        if (date != null) {
            return handleDateSearch(date, role, authenticatedId);
        } else if (month != null) {
            return handleMonthSearch(month, role, authenticatedId);
        }

        log.error("날짜 또는 월 파라미터가 필요합니다");
        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
    }

    private ResponseEntity<List<ScheduleResponseDto>> handleMonthSearch(
            String month, String role, Long authenticatedId) {
        try {
            String[] monthParts = month.split("-");
            if (monthParts.length != 2) {
                log.error("잘못된 월 형식: {}", month);
                throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
            }

            int year = Integer.parseInt(monthParts[0]);
            int monthValue = Integer.parseInt(monthParts[1]);

            log.debug("일정 조회 - 년월: {}-{}", year, monthValue);

            List<ScheduleResponseDto> result;
            if (role.equals("ROLE_TRAINER")) {
                result = scheduleService.getSchedulesByMonthAndTrainer(year, monthValue, authenticatedId);
                log.debug("트레이너 {} - {}개의 일정 조회됨", authenticatedId, result.size());
            } else {
                result = scheduleService.getSchedulesByMonthAndMember(year, monthValue, authenticatedId);
                log.debug("회원 {} - {}개의 일정 조회됨", authenticatedId, result.size());
            }

            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            log.error("월 파싱 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    private ResponseEntity<List<ScheduleResponseDto>> handleDateSearch(
            String date, String role, Long authenticatedId) {
        try {
            String[] dateParts = date.split("-");
            if (dateParts.length != 3) {
                log.error("잘못된 날짜 형식: {}", date);
                throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
            }

            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);

            log.debug("일정 조회 - 날짜: {}-{}-{}", year, month, day);

            List<ScheduleResponseDto> result;
            if (role.equals("ROLE_TRAINER")) {
                result = scheduleService.getSchedulesByDateAndTrainer(year, month, day, authenticatedId);
                log.debug("트레이너 {} - {}개의 일정 조회됨", authenticatedId, result.size());
            } else {
                result = scheduleService.getSchedulesByDateAndMember(year, month, day, authenticatedId);
                log.debug("회원 {} - {}개의 일정 조회됨", authenticatedId, result.size());
            }

            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            log.error("날짜 파싱 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

}