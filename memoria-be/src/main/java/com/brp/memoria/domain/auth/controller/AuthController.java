package com.brp.memoria.domain.auth.controller;

import com.brp.memoria.domain.auth.dto.*;
import com.brp.memoria.domain.auth.service.AuthService;
import com.brp.memoria.domain.auth.service.KakaoOAuthService;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final int REFRESH_TOKEN_MAX_AGE = 30 * 24 * 60 * 60; // 30일 (초)

    private final AuthService authService;
    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        TokenResponse tokenResponse = authService.signUp(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString())
                .body(ApiResponse.ok(tokenResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString())
                .body(ApiResponse.ok(tokenResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        TokenResponse tokenResponse = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString())
                .body(ApiResponse.ok(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie().toString())
                .body(ApiResponse.ok());
    }

    @PostMapping("/oauth/kakao")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoCallback(@Valid @RequestBody OAuthCallbackRequest request) {
        TokenResponse tokenResponse = kakaoOAuthService.processKakaoLogin(request.getCode(), request.getRedirectUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString())
                .body(ApiResponse.ok(tokenResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMe() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(ApiResponse.ok(MemberResponse.from(member)));
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }
}
