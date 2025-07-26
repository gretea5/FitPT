package com.sahur.fitptadmin.domain.auth.service;

import com.sahur.fitptadmin.db.entity.Admin;
import com.sahur.fitptadmin.db.repository.AdminRepository;
import com.sahur.fitptadmin.domain.auth.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sahur.fitptadmin.core.constant.SessionConst.LOGIN_ADMIN_ID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {

    private final AdminRepository adminRepository;

    @Override
    public Long login(LoginRequestDto loginRequestDto) {

        Admin admin = adminRepository.findByAdminLoginId(loginRequestDto.getLoginId());

        if (admin == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (!admin.getAdminPw().equals(loginRequestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return admin.getAdminId();

    }
}
