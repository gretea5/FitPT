package com.sahur.fitptadmin.domain.auth.controller;

import com.sahur.fitptadmin.core.constant.SessionConst;
import com.sahur.fitptadmin.domain.auth.dto.LoginRequestDto;
import com.sahur.fitptadmin.domain.auth.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final HttpSession session;

    @GetMapping("/login")
    public String loginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginRequestDto loginRequestDto, Model model) {
        try {
            Long adminId = loginService.login(loginRequestDto);

            // 로그인 성공 시 세션에 저장
            session.setAttribute(SessionConst.LOGIN_ADMIN_ID, adminId);

            // 성공 시에는 redirect
            return "redirect:/admin/trainers";

        } catch (IllegalArgumentException e) {
            // 실패 시에는 redirect 없이 다시 login 화면 렌더링
            model.addAttribute("errorMsg", e.getMessage());
            return "admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
