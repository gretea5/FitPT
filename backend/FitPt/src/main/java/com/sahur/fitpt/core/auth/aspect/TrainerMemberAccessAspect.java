package com.sahur.fitpt.core.auth.aspect;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TrainerMemberAccessAspect {
    private final MemberRepository memberRepository;

    @Around("@annotation(com.sahur.fitpt.core.auth.annotation.TrainerMemberAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        // 현재 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        Long authenticatedId = Long.parseLong(authentication.getName());

        log.debug("인증된 사용자 정보 - ID: {}, 역할: {}", authenticatedId, role);

        // 요청된 회원 ID 추출
        Long memberId = extractMemberId(joinPoint);
        log.debug("요청된 회원 ID: {}", memberId);

        // 회원인 경우 본인 확인
        if ("ROLE_MEMBER".equals(role)) {
            log.debug("회원 본인 확인 - 요청 ID: {}, 인증된 ID: {}", memberId, authenticatedId);
            if (memberId.equals(authenticatedId)) {
                return joinPoint.proceed();
            }
            log.debug("회원 본인 확인 실패");
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 트레이너인 경우 본인 또는 담당 회원 확인
        if ("ROLE_TRAINER".equals(role)) {
            // 트레이너 본인 확인
            if (memberId.equals(authenticatedId)) {
                log.debug("트레이너 본인 확인 성공");
                return joinPoint.proceed();
            }

            // 담당 회원 확인
            boolean hasAccess = memberRepository.existsByMemberIdAndTrainerTrainerId(
                    memberId, authenticatedId);
            log.debug("트레이너의 담당 회원 확인 - 회원 ID: {}, 트레이너 ID: {}, 접근 권한: {}",
                    memberId, authenticatedId, hasAccess);

            if (hasAccess) {
                return joinPoint.proceed();
            }

            log.debug("트레이너 접근 권한 없음");
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        log.debug("알 수 없는 역할: {}", role);
        throw new CustomException(ErrorCode.FORBIDDEN);
    }

    private Long extractMemberId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.debug("메서드 파라미터: {}", Arrays.toString(args));

        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
    }
}