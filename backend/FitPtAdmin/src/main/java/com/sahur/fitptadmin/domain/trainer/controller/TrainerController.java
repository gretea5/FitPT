package com.sahur.fitptadmin.domain.trainer.controller;

import com.sahur.fitptadmin.core.constant.SessionConst;
import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.domain.trainer.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final HttpSession session;

    @GetMapping("/trainers")
    public String trainers(Model model) {
        // 세션에서 로그인된 Admin ID 가져오기
        Long adminId = (Long) session.getAttribute(SessionConst.LOGIN_ADMIN_ID);

        if (adminId == null) {
            // 세션에 adminId가 없으면 로그인 페이지로 리다이렉트
            return "admin/login";
        }
        // 소속 트레이너 리스트 조회
        List<Trainer> trainerList = trainerService.getTrainers(adminId);

        model.addAttribute("trainers", trainerList);

        // trainers.html 뷰로 이동
        return "admin/trainers";
    }

}
