package com.sahur.fitptadmin.domain.trainer.controller;

import com.sahur.fitptadmin.core.constant.SessionConst;
import com.sahur.fitptadmin.db.entity.Trainer;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerRegisterDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerResponseDto;
import com.sahur.fitptadmin.domain.trainer.dto.TrainerUpdateRequestDto;
import com.sahur.fitptadmin.domain.trainer.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        List<TrainerResponseDto> trainerList = trainerService.getTrainers(adminId);

        model.addAttribute("trainers", trainerList);

        // trainers.html 뷰로 이동
        return "admin/trainers";
    }

    @PostMapping("/trainers")
    public String registerTrainer(@ModelAttribute TrainerRegisterDto trainerRegisterDto) {
        Long adminId = (Long) session.getAttribute(SessionConst.LOGIN_ADMIN_ID);

        if (adminId == null) {
            throw new IllegalStateException("로그인된 관리자 정보가 없습니다.");
        }

        trainerRegisterDto.setAdminId(adminId);
        trainerService.registerTrainer(trainerRegisterDto);
        return "redirect:/admin/trainers";
    }

    @PostMapping("/trainers/{trainerId}")
    public String updateTrainer(@PathVariable Long trainerId,
                                @ModelAttribute TrainerUpdateRequestDto dto) {

        trainerService.updateTrainerInfo(trainerId, dto);
        return "redirect:/admin/trainers";
    }

    @PostMapping("/trainers/{trainerId}/delete")
    public String deleteTrainer(@PathVariable Long trainerId) {
        trainerService.deleteTrainer(trainerId);
        return "redirect:/admin/trainers";
    }

}
