package com.sahur.fitptadmin.domain.member.controller;

import com.sahur.fitptadmin.core.constant.SessionConst;
import com.sahur.fitptadmin.domain.member.dto.MemberDto;
import com.sahur.fitptadmin.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final HttpSession session;

    @GetMapping("/members")
    public String getMembers(Model model) {
        Long adminId = (Long) session.getAttribute(SessionConst.LOGIN_ADMIN_ID);

        if (adminId == null) {
            // 세션에 adminId가 없으면 로그인 페이지로 리다이렉트
            return "admin/login";
        }

        List<MemberDto> memberList =  memberService.getMembers(adminId);
        model.addAttribute("memberList", memberList);

        return "admin/members";
    }

}
