package com.brp.memoria.domain.diary.controller;

import com.brp.memoria.domain.diary.dto.InvitationResponse;
import com.brp.memoria.domain.diary.dto.InviteAcceptRequest;
import com.brp.memoria.domain.diary.dto.InviteRequest;
import com.brp.memoria.domain.diary.service.InvitationService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/api/diaries/{id}/invite")
    public ResponseEntity<ApiResponse<InvitationResponse>> invite(
            @PathVariable Long id,
            @Valid @RequestBody InviteRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        InvitationResponse response = invitationService.invite(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/invitations/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvite(
            @Valid @RequestBody InviteAcceptRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        invitationService.acceptInvite(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/api/invitations/pending")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyInvitations() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<InvitationResponse> response = invitationService.getMyInvitations(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
