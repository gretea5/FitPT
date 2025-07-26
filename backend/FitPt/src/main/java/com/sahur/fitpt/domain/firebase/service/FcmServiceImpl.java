package com.sahur.fitpt.domain.firebase.service;

import com.sahur.fitpt.db.entity.FcmToken;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.FcmTokenRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.domain.firebase.dto.FcmRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long registerFcmToken(FcmRequestDto fcmRequestDto) {
        Member member = memberRepository.findById(fcmRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

//        boolean exists = fcmTokenRepository
//                .findByTokenAndMacAddr(fcmRequestDto.getToken(), fcmRequestDto.getMacAddr())
//                .isPresent();
//
//        if (!exists) {
//            FcmToken fcmToken = FcmToken.builder()
//                    .member(member)
//                    .token(fcmRequestDto.getToken())
//                    .build();
//
//            fcmTokenRepository.save(fcmToken);
//        }

        FcmToken fmToken = FcmToken.builder()
                .member(member)
                .token(fcmRequestDto.getToken())
                .build();

        fcmTokenRepository.save(fmToken);

        return member.getMemberId();
    }
}
