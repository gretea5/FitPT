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
     */
    @Transactional
    public KakaoLoginResponseDto kakaoLogin(String kakaoAccessToken) {
        // 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 회원 조회 또는 생성
        Member member = memberRepository.findByKakaoId(kakaoUserInfo.getId())
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
     */
    private Member createMember(KakaoUserInfo kakaoUserInfo) {
        Member member = Member.builder()
                .kakaoId(kakaoUserInfo.getId())
                .memberName(kakaoUserInfo.getProperties().getNickname())
                .role(Role.MEMBER)
                .isDeleted(false)
                .build();

        return memberRepository.save(member);
    }

    /**
     * Refresh 토큰 저장
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
     */
    public KakaoLoginResponseDto refreshToken(String refreshToken) {
        // Refresh 토큰에서 memberId 추출
        Long memberId = jwtUtil.getMemberId(refreshToken);

        // Redis에서 저장된 Refresh 토큰 조회
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 새로운 토큰 세트 발급
        String newAccessToken = jwtUtil.createAccessToken(memberId, member.getRole().getKey());
        String newRefreshToken = jwtUtil.createRefreshToken();

        // Redis 업데이트
        storeRefreshToken(memberId, newRefreshToken);

        return KakaoLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}