package com.brp.memoria.domain.event.controller;

import com.brp.memoria.domain.event.dto.CommentCreateRequest;
import com.brp.memoria.domain.event.dto.CommentResponse;
import com.brp.memoria.domain.event.service.CommentService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/events/{eventId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long eventId,
            @Valid @RequestBody CommentCreateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        CommentResponse response = commentService.addComment(memberId, eventId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/events/{eventId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long eventId) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<CommentResponse> response = commentService.getComments(memberId, eventId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        commentService.deleteComment(memberId, commentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
