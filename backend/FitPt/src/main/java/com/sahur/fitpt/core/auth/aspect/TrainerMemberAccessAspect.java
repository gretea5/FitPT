package com.sahur.fitpt.core.auth.aspect;

import com.sahur.fitpt.core.auth.annotation.TrainerMemberAccess;
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
import com.sahur.fitpt.db.repository.ScheduleRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TrainerMemberAccessAspect {
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    @Around("@annotation(trainerMemberAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint, TrainerMemberAccess trainerMemberAccess) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Long authenticatedId = Long.parseLong(authentication.getName());

        log.debug("Role: {}, AuthenticatedId: {}, Domain: {}, AccessType: {}",
                role, authenticatedId, trainerMemberAccess.domain(), trainerMemberAccess.accessType());

        // 트레이너 전용 접근 체크
        if (trainerMemberAccess.accessType() == TrainerMemberAccess.AccessType.TRAINER_ONLY
                && !"ROLE_TRAINER".equals(role)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // memberId 파라미터 체크 (PathVariable)
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Long memberId && trainerMemberAccess.accessType() != TrainerMemberAccess.AccessType.TRAINER_ONLY) {
            if ("ROLE_TRAINER".equals(role)) {
                // 트레이너인 경우 담당 회원 체크
                boolean hasAccess = memberRepository.existsByMemberIdAndTrainerTrainerId(memberId, authenticatedId);
                if (!hasAccess) {
                    throw new CustomException(ErrorCode.ACCESS_DENIED);
                }
            } else if ("ROLE_MEMBER".equals(role)) {
                // 회원인 경우 본인 정보 체크
                if (!memberId.equals(authenticatedId)) {
                    throw new CustomException(ErrorCode.ACCESS_DENIED);
                }
            }
        }

        // Schedule 도메인 특별 처리
        if (trainerMemberAccess.domain() == TrainerMemberAccess.Domain.SCHEDULE) {
            if ("ROLE_TRAINER".equals(role)) {
                // scheduleId가 있는 경우 (PUT, DELETE) 해당 일정의 트레이너 체크
                for (Object arg : args) {
                    if (arg instanceof Long scheduleId) {
                        boolean hasAccess = scheduleRepository.existsByScheduleIdAndTrainerTrainerId(
                                scheduleId, authenticatedId);
                        if (!hasAccess) {
                            throw new CustomException(ErrorCode.ACCESS_DENIED);
                        }
                        break;
                    }
                }
            }
        }

        return joinPoint.proceed();
    }
}