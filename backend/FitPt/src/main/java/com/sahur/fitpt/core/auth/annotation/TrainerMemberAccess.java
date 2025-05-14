package com.sahur.fitpt.core.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrainerMemberAccess {
    enum Domain {
        SCHEDULE,
        MEMBER
    }

    enum AccessType {
        TRAINER_ONLY,    // 트레이너만 접근 가능
        BOTH_ALLOWED    // 트레이너와 회원 모두 접근 가능
    }

    Domain domain() default Domain.MEMBER;
    AccessType accessType() default AccessType.TRAINER_ONLY;
}