package com.sahur.fitpt.domain.composition.service;

import com.sahur.fitpt.core.constant.ErrorCode;
import com.sahur.fitpt.core.exception.CustomException;
import com.sahur.fitpt.db.entity.CompositionLog;
import com.sahur.fitpt.db.entity.Member;
import com.sahur.fitpt.db.repository.CompositionRepository;
import com.sahur.fitpt.db.repository.MemberRepository;
import com.sahur.fitpt.domain.composition.dto.CompositionDetailResponseDto;
import com.sahur.fitpt.domain.composition.dto.CompositionRequestDto;
import com.sahur.fitpt.domain.composition.dto.CompositionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompositionServiceImpl implements CompositionService {
    private final CompositionRepository compositionRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long saveComposition(CompositionRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CompositionLog compositionLog = CompositionLog.builder()
                .member(member)
                .protein(request.getProtein())
                .bmr(request.getBmr())
                .mineral(request.getMineral())
                .bodyAge(request.getBodyAge())
                .smm(request.getSmm())
                .icw(request.getIcw())
                .ecw(request.getEcw())
                .bfm(request.getBfm())
                .bfp(request.getBfp())
                .weight(request.getWeight())
                .build();

        CompositionLog savedCompositionLog = compositionRepository.save(compositionLog);

        return savedCompositionLog.getCompositionLogId();
    }

    @Override
    public CompositionDetailResponseDto getCompositionById(Long compositionLogId) {
        if (compositionLogId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }
        CompositionLog compositionLog = compositionRepository.findById(compositionLogId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPOSITION_NOT_FOUND));
        // 측정한 유저 정보
        Member member = compositionLog.getMember();
        // 유저 성별
        String gender = member.getMemberGender();
        // 몸무게
        float weight = compositionLog.getWeight();
        // 키
        float height = (member.getMemberHeight()) / 100f;
        // bmi 계산
        float bmi = calculateBMI(height, weight);

        return CompositionDetailResponseDto.builder()
                .compositionLogId(compositionLog.getCompositionLogId())
                .memberId(member.getMemberId())
                .createdAt(compositionLog.getCreatedAt())
                .protein(compositionLog.getProtein())
                .bmr(compositionLog.getBmr())
                .mineral(compositionLog.getMineral())
                .bodyAge(compositionLog.getBodyAge())
                .smm(compositionLog.getSmm())
                .icw(compositionLog.getIcw())
                .ecw(compositionLog.getEcw())
                .bfm(compositionLog.getBfm())
                .bfp(compositionLog.getBfp())
                .weight(compositionLog.getWeight())
                // 분류 결과
                .weightClassify(classifyWeight(height, weight, gender))
                .weightColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과", "많이 초과")))
                .bfmClassify(classifyBodyFatMass(compositionLog.getBfm(), height, gender))
                .bfmColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과", "많이 초과")))
                .bfpClassify(classifyBodyFatPercentage(compositionLog.getBfp(), gender))
                .bfpColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과", "많이 초과")))
                .smmClassify(classifySkeletalMuscleMass(compositionLog.getSmm(), gender, height))
                .smmColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과", "많이 초과")))
                .bmiClassify(classifyBMI(bmi))
                .bmiColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과", "많이 초과")))
                .tcwClassify(classifyTCW(compositionLog.getIcw(), compositionLog.getEcw(), weight, gender))
                .tcwColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과")))
                .proteinClassify(classifyProtein(compositionLog.getProtein(), weight, gender))
                .proteinColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과")))
                .mineralClassify(classifyMineral(compositionLog.getMineral(), weight, gender))
                .mineralColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과")))
                .ecwRatioClassify(classifyECWRatio(compositionLog.getEcw(), compositionLog.getIcw(), gender))
                .ecwRatioColumn(new ArrayList<>(Arrays.asList("미만", "적정", "초과")))
                .build();
    }

    @Override
    public List<CompositionResponseDto> getCompositionsByMemberId(Long memberId) {
        return getCompositionsByMemberId(memberId, null, null);
    }

    @Override
    public List<CompositionResponseDto> getCompositionsByUserIdWithSort(Long memberId, String sortField, String order) {
        return getCompositionsByMemberId(memberId, sortField, order);
    }

    public List<CompositionResponseDto> getCompositionsByMemberId(Long memberId, String sortField, String order) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_NULL_OR_EMPTY_VALUE);
        }

        if (sortField != null && order != null) {
            Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortField);
            return compositionRepository.findAllByMemberMemberId(memberId, sort)
                    .stream()
                    .map(CompositionResponseDto::fromEntity)
                    .toList();
        } else {
            return compositionRepository.findAllByMemberMemberId(memberId)
                    .stream()
                    .map(CompositionResponseDto::fromEntity)
                    .toList();
        }
    }

    // BMI 계산
    private float calculateBMI(float height, float weight) {
        return weight / (height * height);
    }

    private String classifyBMI(float bmi) {
        if (bmi < 18.5) {
            return "미만";
        } else if (bmi < 23.0) {
            return "적정";
        } else if (bmi < 25.0) {
            return "초과";
        } else {
            return "많이 초과";
        }
    }

    // 적정 체지방률 분류
    private String classifyBodyFatPercentage(float bfp, String gender) {
        if (gender.equals("남성")) {
            if (bfp < 10) return "미만";
            else if (bfp <= 19) return "적정";
            else if (bfp <= 24) return "초과";
            else return "많이 초과";
        } else {
            if (bfp < 18) return "미만";
            else if (bfp <= 28) return "적정";
            else if (bfp <= 34) return "초과";
            else return "많이 초과";
        }
    }

    // 적정 체지방량 분류
    private String classifyBodyFatMass(float bfm, float height, String gender) {
        if (gender.equals("남성")) {
            if (height <= 165) {
                if (bfm < 9.0) return "미만";
                else if (bfm <= 13.0) return "적정";
                else if (bfm <= 18.0) return "초과";
                else return "많이 초과";
            } else if (height <= 175) {
                if (bfm < 10.0) return "미만";
                else if (bfm <= 15.0) return "적정";
                else if (bfm <= 20.0) return "초과";
                else return "많이 초과";
            } else {
                if (bfm < 12.0) return "미만";
                else if (bfm <= 17.0) return "적정";
                else if (bfm <= 22.0) return "초과";
                else return "많이 초과";
            }
        } else {
            if (height <= 155) {
                if (bfm < 11.0) return "미만";
                else if (bfm <= 18.0) return "적정";
                else if (bfm <= 23.0) return "초과";
                else return "많이 초과";
            } else if (height <= 165) {
                if (bfm < 13.0) return "미만";
                else if (bfm <= 20.0) return "적정";
                else if (bfm <= 25.0) return "초과";
                else return "많이 초과";
            } else {
                if (bfm < 14.0) return "미만";
                else if (bfm <= 22.0) return "적정";
                else if (bfm <= 27.0) return "초과";
                else return "많이 초과";
            }
        }
    }

    // 적정 골격근량 분류
    private String classifySkeletalMuscleMass(float smm, String gender, float height) {
        if (gender.equals("남성")) {
            if (height <= 165) {
                if (smm < 27.0) return "미만";
                else if (smm <= 32.0) return "적정";
                else if (smm <= 37.0) return "초과";
                else return "많이 초과";
            } else if (height <= 175) {
                if (smm < 28.0) return "미만";
                else if (smm <= 33.0) return "적정";
                else if (smm <= 38.0) return "초과";
                else return "많이 초과";
            } else {
                if (smm < 30.0) return "미만";
                else if (smm <= 35.0) return "적정";
                else if (smm <= 40.0) return "초과";
                else return "많이 초과";
            }
        } else {
            if (height <= 155) {
                if (smm < 18.0) return "미만";
                else if (smm <= 23.0) return "적정";
                else if (smm <= 28.0) return "초과";
                else return "많이 초과";
            } else if (height <= 165) {
                if (smm < 20.0) return "미만";
                else if (smm <= 25.0) return "적정";
                else if (smm <= 30.0) return "초과";
                else return "많이 초과";
            } else {
                if (smm < 22.0) return "미만";
                else if (smm <= 27.0) return "적정";
                else if (smm <= 32.0) return "초과";
                else return "많이 초과";
            }
        }
    }

    // 적정 세포내체수분량 분류
    private String classifyICW(float icw, float weight, String gender) {
        if (gender.equals("남성")) {
            if (weight < 60) {
                if (icw < 20.0) return "미만";
                else if (icw <= 23.0) return "적정";
                else if (icw <= 26.0) return "초과";
                else return "많이 초과";
            } else if (weight <= 80) {
                if (icw < 22.0) return "미만";
                else if (icw <= 25.0) return "적정";
                else if (icw <= 28.0) return "초과";
                else return "많이 초과";
            } else {
                if (icw < 24.0) return "미만";
                else if (icw <= 27.0) return "적정";
                else if (icw <= 30.0) return "초과";
                else return "많이 초과";
            }
        } else {
            if (weight < 50) {
                if (icw < 16.0) return "미만";
                else if (icw <= 19.0) return "적정";
                else if (icw <= 22.0) return "초과";
                else return "많이 초과";
            } else if (weight <= 70) {
                if (icw < 18.0) return "미만";
                else if (icw <= 21.0) return "적정";
                else if (icw <= 24.0) return "초과";
                else return "많이 초과";
            } else {
                if (icw < 20.0) return "미만";
                else if (icw <= 23.0) return "적정";
                else if (icw <= 26.0) return "초과";
                else return "많이 초과";
            }
        }
    }

    // 적정 세포외수분비 분류
    private String classifyECWRatio(float ecw, float icw, String gender) {
        float tbw = ecw + icw;
        float ecwRatio = ecw / tbw;

        if (ecwRatio <= 0.36) {
            return "미만";
        } else if (ecwRatio <= 0.39) {
            return "적정";
        } else {
            return "초과";
        }
    }

    // 적정 단백질 분류
    private String classifyProtein(float protein, float weight, String gender) {
        float ratio = protein / weight;

        if (gender.equals("남성")) {
            if (ratio < 0.12) {
                return "미만";
            } else if (ratio <= 0.16) {
                return "적정";
            } else {
                return "초과";
            }
        } else {
            if (ratio < 0.11) {
                return "미만";
            } else if (ratio <= 0.15) {
                return "적정";
            } else {
                return "초과";
            }
        }
    }

    // 적정 무기질 분류
    private String classifyMineral(float mineral, float weight, String gender) {
        float ratio = mineral / weight;

        if (gender.equals("남성")) {
            if (ratio < 0.045) {
                return "미만";
            } else if (ratio <= 0.06) {
                return "적정";
            } else {
                return "초과";
            }
        } else {
            if (ratio < 0.04) {
                return "미만";
            } else if (ratio <= 0.055) {
                return "적정";
            } else {
                return "초과";
            }
        }
    }

    // 적정 체수분량 분류
    private String classifyTCW(float icw, float ecw, float weight, String gender) {
        float tcwRatio = (icw + ecw) / weight;

        if (gender.equals("남성")) {
            if (tcwRatio < 0.5) {
                return "미만";
            } else if (tcwRatio < 0.65) {
                return "적정";
            } else {
                return "초과";
            }
        } else {
            if (tcwRatio < 0.5) {
                return "미만";
            } else if (tcwRatio < 0.6) {
                return "적정";
            } else {
                return "초과";
            }
        }

    }

    // 적정 체중 분류
    private String classifyWeight(float height, float weight, String gender) {
        float heightMeter = height / 100f;
        float standardWeight = heightMeter * heightMeter * 22;
        float ratio = (weight / standardWeight) * 100;

        if(ratio < 90) {
            return "미만";
        } else if (ratio <= 110) {
            return "적정";
        } else if (ratio <= 120) {
            return "초과";
        } else {
            return "많이 초과";
        }
    }
}