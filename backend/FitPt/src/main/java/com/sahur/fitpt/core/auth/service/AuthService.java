package com.sahur.fitpt.core.auth.service;

import com.sahur.fitpt.core.auth.dto.KakaoLoginResponseDto;
import com.sahur.fitpt.core.auth.dto.KakaoUserInfo;
import com.sahur.fitpt.core.auth.jwt.JWTUtil;
import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.constant.Role;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    /**
     * 카카오 로그인 처리
     * 1. 카카오 액세스 토큰으로 사용자 정보 조회
     * 2. 회원가입 또는 로그인 처리
     * 3. JWT 토큰 발급
     */
    @Transactional
    public KakaoLoginResponseDto kakaoLogin(String kakaoAccessToken) {
        // 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 회원 조회 또는 생성
        Member member = memberRepository.findByMemberName(kakaoUserInfo.getProperties().getNickname())
                .orElseGet(() -> createMember(kakaoUserInfo));

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getRole().getKey());
        String refreshToken = jwtUtil.createRefreshToken();

        // Refresh 토큰 Redis 저장
        storeRefreshToken(member.getMemberId(), refreshToken);

        return KakaoLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 카카오 사용자 정보 조회
     * 카카오 API를 호출하여 사용자 정보를 가져옴
     */
    private KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfo.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_SERVER_ERROR);
        }
    }

    /**
     * 신규 회원 생성
     * 카카오 사용자 정보를 기반으로 새로운 회원 생성
     */
    private Member createMember(KakaoUserInfo kakaoUserInfo) {
        Member member = Member.builder()
                .memberName(kakaoUserInfo.getProperties().getNickname())
                .role(Role.MEMBER) // 기본 역할은 MEMBER로 설정
                .isDeleted(false)
                .build();

        return memberRepository.save(member);
    }

    /**
     * Refresh 토큰 저장
     * Redis에 Refresh 토큰을 저장하고 만료 시간 설정
     */
    private void storeRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RT:" + memberId,
                refreshToken,
                7, // 7일 저장
                TimeUnit.DAYS
        );
    }

    /**
     * 로그아웃 처리
     * Access 토큰을 블랙리스트에 추가하고 Refresh 토큰 삭제
     */
    public void logout(Long memberId, String accessToken) {
        // Access 토큰 블랙리스트 추가
        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                jwtUtil.getExpirationTime(accessToken),
                TimeUnit.MILLISECONDS
        );

        // Refresh 토큰 삭제
        redisTemplate.delete("RT:" + memberId);
    }

    /**
     * 토큰 재발급
     * Refresh 토큰을 검증하고 새로운 Access 토큰 발급
     */
    public String reissueAccessToken(Long memberId, String refreshToken) {
        // Redis에서 저장된 Refresh 토큰 조회
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return jwtUtil.createAccessToken(memberId, member.getRole().getKey());
    }
}