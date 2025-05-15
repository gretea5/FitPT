package com.sahur.fitptadmin.core.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER("ROLE_MEMBER", "회원"),
    TRAINER("ROLE_TRAINER", "트레이너");

    private final String key;
    private final String title;
}
