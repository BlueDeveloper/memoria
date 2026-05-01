package com.brp.memoria.domain.auth.service;

import com.brp.memoria.domain.auth.dto.TokenResponse;
import com.brp.memoria.domain.auth.exception.AuthErrorCode;
import com.brp.memoria.domain.auth.exception.AuthException;
import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.diary.repository.DiaryMemberRepository;
import com.brp.memoria.domain.diary.repository.DiaryRepository;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    @Value("${kakao.client-id:916c4662c0d22467bbb876cf8a77521a}")
    private String kakaoClientId;

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public TokenResponse processKakaoLogin(String code, String redirectUri) {
        // 1. 인가 코드로 카카오 액세스 토큰 교환
        String kakaoAccessToken = getKakaoAccessToken(code, redirectUri);

        // 2. 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);
        log.info("Kakao login: id={}, email={}, nickname={}", userInfo.id, userInfo.email, userInfo.nickname);

        // 3. 회원 조회 또는 생성
        Member member = findOrCreateMember(userInfo);

        // 4. JWT 발급
        return issueTokens(member);
    }

    private String getKakaoAccessToken(String code, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("Failed to get Kakao access token", e);
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL, HttpMethod.GET, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            long id = jsonNode.get("id").asLong();
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            JsonNode profile = kakaoAccount.get("profile");

            String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : null;
            String nickname = profile.has("nickname") ? profile.get("nickname").asText() : "사용자";
            String profileImage = profile.has("profile_image_url") ? profile.get("profile_image_url").asText() : null;

            return new KakaoUserInfo(id, email, nickname, profileImage);
        } catch (Exception e) {
            log.error("Failed to get Kakao user info", e);
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private Member findOrCreateMember(KakaoUserInfo userInfo) {
        String providerId = String.valueOf(userInfo.id);

        // provider + providerId로 조회
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(
                Member.Provider.KAKAO, providerId);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            // 프로필 이미지 업데이트
            if (userInfo.profileImage != null) {
                member.updateProfileImage(userInfo.profileImage);
            }
            return member;
        }

        // 이메일로 기존 회원 조회 (이메일 계정과 카카오 연동)
        if (userInfo.email != null) {
            Optional<Member> emailMember = memberRepository.findByEmail(userInfo.email);
            if (emailMember.isPresent()) {
                // TODO: 기존 이메일 계정에 카카오 연동 (provider/providerId 업데이트 필요)
                return emailMember.get();
            }
        }

        // 신규 회원 생성
        String email = userInfo.email != null ? userInfo.email : "kakao_" + userInfo.id + "@memoria.app";
        Member member = Member.builder()
                .email(email)
                .nickname(userInfo.nickname)
                .profileImage(userInfo.profileImage)
                .provider(Member.Provider.KAKAO)
                .providerId(providerId)
                .role(Member.Role.USER)
                .build();
        memberRepository.save(member);

        // 기본 다이어리 생성
        createDefaultDiary(member);

        return member;
    }

    private void createDefaultDiary(Member member) {
        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Diary diary = Diary.builder()
                .name("나의 다이어리")
                .color("#1B2A4A")
                .inviteCode(inviteCode)
                .owner(member)
                .diaryType("GENERAL")
                .build();
        diaryRepository.save(diary);

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .role(DiaryMember.DiaryRole.OWNER)
                .build();
        diaryMemberRepository.save(diaryMember);
    }

    private TokenResponse issueTokens(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        String redisKey = REFRESH_TOKEN_PREFIX + member.getMemberId();
        redisTemplate.opsForValue().set(
                redisKey, refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return TokenResponse.of(accessToken, refreshToken);
    }

    private record KakaoUserInfo(long id, String email, String nickname, String profileImage) {}
}
