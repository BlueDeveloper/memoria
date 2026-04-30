package com.brp.memoria.domain.calendar.controller;

import com.brp.memoria.domain.calendar.dto.*;
import com.brp.memoria.domain.calendar.service.CalendarService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<ApiResponse<CalendarResponse>> createCalendar(
            @Valid @RequestBody CalendarCreateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        CalendarResponse response = calendarService.createCalendar(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CalendarListResponse>> getMyCalendars() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        CalendarListResponse response = calendarService.getMyCalendars(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CalendarResponse>> getCalendarDetail(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        CalendarResponse response = calendarService.getCalendarDetail(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CalendarResponse>> updateCalendar(
            @PathVariable Long id,
            @Valid @RequestBody CalendarUpdateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        CalendarResponse response = calendarService.updateCalendar(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCalendar(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        calendarService.deleteCalendar(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<CalendarMemberResponse>>> getCalendarMembers(
            @PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<CalendarMemberResponse> response = calendarService.getCalendarMembers(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/members/color")
    public ResponseEntity<ApiResponse<Void>> updateMemberColor(
            @PathVariable Long id,
            @Valid @RequestBody MemberColorUpdateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        calendarService.updateMemberColor(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
