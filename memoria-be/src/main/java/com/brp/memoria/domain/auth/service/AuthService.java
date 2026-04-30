package com.brp.memoria.domain.auth.service;

import com.brp.memoria.domain.auth.dto.LoginRequest;
import com.brp.memoria.domain.auth.dto.SignUpRequest;
import com.brp.memoria.domain.auth.dto.TokenResponse;
import com.brp.memoria.domain.auth.exception.AuthErrorCode;
import com.brp.memoria.domain.auth.exception.AuthException;
import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.diary.repository.DiaryRepository;
import com.brp.memoria.domain.diary.repository.DiaryMemberRepository;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.createEmailMember(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        memberRepository.save(member);

        // 기본 다이어리 자동 생성
        createDefaultDiary(member);

        return issueTokens(member);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());
        try {
            Member member = memberRepository.findByEmail(request.getEmail())
                    .filter(m -> "N".equals(m.getDelYn()))
                    .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

            if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
                throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
            }

            return issueTokens(member);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed", e);
            throw e;
        }
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + memberId;

        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        // 기존 RT 폐기 후 새로 발급 (Rotation)
        redisTemplate.delete(redisKey);

        return issueTokens(member);
    }

    public void logout(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            Long memberId = jwtTokenProvider.getMemberId(refreshToken);
            String redisKey = REFRESH_TOKEN_PREFIX + memberId;
            redisTemplate.delete(redisKey);
        }
    }

    private void createDefaultDiary(Member member) {
        String inviteCode = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
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

        // Redis에 Refresh Token 저장
        String redisKey = REFRESH_TOKEN_PREFIX + member.getMemberId();
        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return TokenResponse.of(accessToken, refreshToken);
    }
}
