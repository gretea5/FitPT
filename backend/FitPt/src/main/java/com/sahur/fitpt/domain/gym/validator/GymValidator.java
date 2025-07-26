package com.sahur.fitpt.domain.gym.validator;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import org.springframework.stereotype.Component;

@Component
public class GymValidator {

    public void validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }
    }
}