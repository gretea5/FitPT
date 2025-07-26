package com.sahur.fitptadmin.domain.auth.service;

import com.sahur.fitptadmin.domain.auth.dto.LoginRequestDto;

public interface LoginService {

    public Long login(LoginRequestDto loginRequestDto);
}
