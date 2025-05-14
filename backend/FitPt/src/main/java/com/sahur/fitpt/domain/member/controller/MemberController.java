package com.sahur.fitpt.domain.member.controller;

import com.sahur.fitpt.core.auth.annotation.TrainerMemberAccess;
import com.sahur.fitpt.domain.member.dto.MemberRequestDto;
import com.sahur.fitpt.domain.member.dto.MemberResponseDto;
import com.sahur.fitpt.domain.member.dto.MemberPartialUpdateDto;
import com.sahur.fitpt.domain.member.dto.MemberSignUpResponseDto;
import com.sahur.fitpt.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
@Validated  // 추가
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<MemberSignUpResponseDto> createMember(
            @Valid @RequestBody MemberRequestDto requestDto) {
        return ResponseEntity.ok(memberService.createMember(requestDto));
    }

    @GetMapping("/{memberId}")
    @TrainerMemberAccess
    @Operation(summary = "회원 정보 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ResponseEntity<MemberResponseDto> getMember(
            @Parameter(description = "조회할 회원 ID", required = true)
            @PathVariable @Min(value = 1, message = "회원 ID는 1 이상이어야 합니다") Long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @PutMapping("/{memberId}")
    @TrainerMemberAccess
    @Operation(summary = "회원 정보 전체 수정", description = "회원의 모든 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ResponseEntity<MemberResponseDto> updateMember(
            @Parameter(description = "수정할 회원 ID", required = true)
            @PathVariable @Min(value = 1, message = "회원 ID는 1 이상이어야 합니다") Long memberId,
            @Valid @RequestBody MemberRequestDto requestDto) {
        return ResponseEntity.ok(memberService.updateMember(memberId, requestDto));
    }

    @PatchMapping("/{memberId}")
    @TrainerMemberAccess
    @Operation(summary = "회원 정보 부분 수정", description = "회원 정보를 부분적으로 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ResponseEntity<MemberResponseDto> updateMemberPartially(
            @Parameter(description = "수정할 회원 ID", required = true)
            @PathVariable @Min(value = 1, message = "회원 ID는 1 이상이어야 합니다") Long memberId,
            @Valid @RequestBody MemberPartialUpdateDto updateDto) {
        return ResponseEntity.ok(memberService.updateMemberPartially(memberId, updateDto));
    }

    @DeleteMapping("/{memberId}")
    @TrainerMemberAccess
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴 처리합니다. (논리적 삭제)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ResponseEntity<Long> deleteMember(
            @Parameter(description = "탈퇴할 회원 ID", required = true)
            @PathVariable @Min(value = 1, message = "회원 ID는 1 이상이어야 합니다") Long memberId) {
        return ResponseEntity.ok(memberService.deleteMember(memberId));
    }

    @GetMapping
    @Operation(summary = "담당 회원 목록 조회", description = "현재 로그인한 트레이너의 담당 회원 목록을 조회합니다.")
    @TrainerMemberAccess
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없음")
    })
    public ResponseEntity<List<MemberResponseDto>> getMyMembers(
            @Parameter(hidden = true) @RequestAttribute("trainerId") Long trainerId
    ) {
        return ResponseEntity.ok(memberService.getMyMembers(trainerId));
    }
}