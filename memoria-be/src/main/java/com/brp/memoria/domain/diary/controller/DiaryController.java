package com.brp.memoria.domain.diary.controller;

import com.brp.memoria.domain.diary.dto.*;
import com.brp.memoria.domain.diary.service.DiaryService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponse>> createDiary(
            @Valid @RequestBody DiaryCreateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        DiaryResponse response = diaryService.createDiary(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DiaryListResponse>> getMyDiaries() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        DiaryListResponse response = diaryService.getMyDiaries(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponse>> getDiaryDetail(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        DiaryResponse response = diaryService.getDiaryDetail(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponse>> updateDiary(
            @PathVariable Long id,
            @Valid @RequestBody DiaryUpdateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        DiaryResponse response = diaryService.updateDiary(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDiary(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        diaryService.deleteDiary(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<DiaryMemberResponse>>> getDiaryMembers(
            @PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<DiaryMemberResponse> response = diaryService.getDiaryMembers(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/members/color")
    public ResponseEntity<ApiResponse<Void>> updateMemberColor(
            @PathVariable Long id,
            @Valid @RequestBody MemberColorUpdateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        diaryService.updateMemberColor(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
