package com.sahur.fitptadmin.domain.auth.controller;

import com.sahur.fitptadmin.domain.auth.dto.LoginRequestDto;
import com.sahur.fitptadmin.domain.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginRequestDto loginRequestDto, Model model) {
        try {
            Long adminId = loginService.login(loginRequestDto);
            model.addAttribute("adminId", adminId);
            return "admin/login-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "admin/login";
        }
    }


}
