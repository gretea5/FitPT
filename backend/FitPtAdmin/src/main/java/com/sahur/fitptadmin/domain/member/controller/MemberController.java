package com.sahur.fitptadmin.domain.member.controller;

import com.sahur.fitptadmin.core.constant.SessionConst;
import com.sahur.fitptadmin.domain.member.dto.MemberDto;
import com.sahur.fitptadmin.domain.member.service.MemberService;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import com.sahur.fitptadmin.domain.trainer.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TrainerService trainerService;
    private final HttpSession session;

    @GetMapping("/members")
    public String getMembers(Model model) {
        Long adminId = (Long) session.getAttribute(SessionConst.LOGIN_ADMIN_ID);

        if (adminId == null) {
            // 세션에 adminId가 없으면 로그인 페이지로 리다이렉트
            return "admin/login";
        }

        List<MemberDto> memberList = memberService.getMembers(adminId);
        List<TrainerResponseDto> trainerList = trainerService.getTrainers(adminId);

        model.addAttribute("memberList", memberList);
        model.addAttribute("trainerList", trainerList);

        return "admin/members";
    }

    @PostMapping("/members/{memberId}/update-trainer")
    public String updateTrainer(
            @PathVariable Long memberId,
            @RequestParam(required = false) Long trainerId,
            RedirectAttributes redirectAttributes
    ) {
        memberService.changeTrainer(memberId, trainerId);

        redirectAttributes.addFlashAttribute("successMsg", "트레이너가 성공적으로 변경되었습니다.");
        return "redirect:/admin/members";
    }

}
