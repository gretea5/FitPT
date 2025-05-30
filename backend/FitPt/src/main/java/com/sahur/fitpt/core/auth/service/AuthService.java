package com.sahur.fitpt.core.auth.service;

import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.dto.KakaoSignupRequestDto;
import com.sahur.fitpt.core.auth.dto.KakaoSignupResponseDto;
import com.sahur.fitpt.core.auth.dto.KakaoUserInfo;
import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.constant.Role;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Admin;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.db.repository.AdminRepository;
import com.sahur.fitpt.db.repository.FcmTokenRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final FcmTokenRepository fcmTokenRepository;

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    @Transactional
    public KakaoSignupResponseDto kakaoSignup(KakaoSignupRequestDto request) {
        // 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(request.getKakaoAccessToken());
        log.debug("카카오 사용자 정보 조회 성공 - id: {}", kakaoUserInfo.getId());

        // 기존 회원 조회 (삭제 여부 상관없이)
        Optional<Member> existingMember = memberRepository.findByKakaoId(kakaoUserInfo.getId());
        log.debug("기존 회원 조회 결과 - exists: {}, isDeleted: {}",
                existingMember.isPresent(),
                existingMember.map(Member::isDeleted).orElse(false));

        Member savedMember;

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (member.isDeleted()) {
                // 탈퇴한 회원인 경우 정보 업데이트 및 재활성화
                Admin admin = null;
                if (request.getAdminId() != null) {
                    admin = adminRepository.findById(request.getAdminId())
                            .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
                }

                member.update(
                        request.getMemberName(),
                        request.getMemberGender(),
                        request.getMemberBirth(),
                        request.getMemberHeight(),
                        request.getMemberWeight(),
                        null,  // trainer는 null로 초기화
                        admin
                );
                member.reactivate();
                savedMember = memberRepository.save(member);
                log.info("탈퇴한 회원이 재가입했습니다: id={}, name={}",
                        savedMember.getMemberId(), savedMember.getMemberName());
            } else {
                log.error("이미 존재하는 회원입니다 - kakaoId: {}", kakaoUserInfo.getId());
                throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
            }
        } else {
            // 신규 회원 가입 로직
            Admin admin = null;
            if (request.getAdminId() != null) {
                admin = adminRepository.findById(request.getAdminId())
                        .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
            }

            Member member = Member.builder()
                    .kakaoId(kakaoUserInfo.getId())
                    .memberName(request.getMemberName())
                    .memberGender(request.getMemberGender())
                    .memberBirth(request.getMemberBirth())
                    .memberHeight(request.getMemberHeight())
                    .memberWeight(request.getMemberWeight())
                    .admin(admin)
                    .role(Role.MEMBER)
                    .isDeleted(false)
                    .build();

            savedMember = memberRepository.save(member);
            log.info("새로운 회원이 등록되었습니다: id={}, name={}",
                    savedMember.getMemberId(), savedMember.getMemberName());
        }

        // FCM 토큰 처리
        if (request.getFcmToken() != null) {
            FcmToken fcmToken = FcmToken.builder()
                    .member(savedMember)
                    .token(request.getFcmToken())
                    .macAddr(null)
                    .build();
            fcmTokenRepository.save(fcmToken);
        }

        // JWT 토큰 생성 및 응답
        String accessToken = jwtUtil.createAccessToken(
                savedMember.getMemberId(),
                savedMember.getRole().getKey()
        );
        String refreshToken = jwtUtil.createRefreshToken();

        storeRefreshToken(savedMember.getMemberId(), refreshToken);

        return KakaoSignupResponseDto.builder()
                .memberId(savedMember.getMemberId())
                .memberName(savedMember.getMemberName())
                .memberGender(savedMember.getMemberGender())
                .memberBirth(savedMember.getMemberBirth())
                .memberHeight(savedMember.getMemberHeight())
                .memberWeight(savedMember.getMemberWeight())
                .adminId(savedMember.getAdmin() != null ? savedMember.getAdmin().getAdminId() : null)
                .gymName(savedMember.getAdmin() != null ? savedMember.getAdmin().getGymName() : null)
                .fcmToken(request.getFcmToken())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public KakaoLoginResponseDto kakaoLogin(String kakaoAccessToken, String fcmToken) {
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        Member member = memberRepository.findByKakaoId(kakaoUserInfo.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        log.debug("로그인 회원 정보: id={}, name={}", member.getMemberId(), member.getMemberName());

        // FCM 토큰 처리
        if (fcmToken != null) {
            List<FcmToken> existingTokens = fcmTokenRepository.findByMember_MemberId(member.getMemberId());

            // 동일한 토큰이 없을 경우에만 새로 저장
            if (existingTokens.stream().noneMatch(token -> token.getToken().equals(fcmToken))) {
                FcmToken newFcmToken = FcmToken.builder()
                        .member(member)
                        .token(fcmToken)
                        .macAddr(null)
                        .build();
                fcmTokenRepository.save(newFcmToken);
            }
        }

        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getRole().getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        storeRefreshToken(member.getMemberId(), refreshToken);


        return KakaoLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        try {
            log.debug("카카오 사용자 정보 요청 시작");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfo.class
            );

            if (response.getBody() == null) {
                log.error("카카오 서버 응답에 body가 없습니다");
                throw new CustomException(ErrorCode.KAKAO_SERVER_ERROR);
            }
            log.debug("카카오 사용자 정보 요청 성공");
            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 서버 연동 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_SERVER_ERROR);
        }
    }

    private void storeRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RT:" + memberId,
                refreshToken,
                7,
                TimeUnit.DAYS
        );
    }

    public void logout(Long memberId, String accessToken) {
        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                jwtUtil.getExpirationTime(accessToken),
                TimeUnit.MILLISECONDS
        );

        redisTemplate.delete("RT:" + memberId);
    }

    public KakaoLoginResponseDto refreshToken(String refreshToken) {
        Long memberId = jwtUtil.getMemberId(refreshToken);

        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(memberId, member.getRole().getKey());
        String newRefreshToken = jwtUtil.createRefreshToken();

        storeRefreshToken(memberId, newRefreshToken);

        return KakaoLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}