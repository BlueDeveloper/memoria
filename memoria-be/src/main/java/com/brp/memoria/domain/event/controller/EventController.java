package com.brp.memoria.domain.event.controller;

import com.brp.memoria.domain.event.dto.EventCreateRequest;
import com.brp.memoria.domain.event.dto.EventListResponse;
import com.brp.memoria.domain.event.dto.EventResponse;
import com.brp.memoria.domain.event.dto.EventUpdateRequest;
import com.brp.memoria.domain.event.service.EventService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/api/events")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        EventResponse response = eventService.createEvent(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/calendars/{calendarId}/events")
    public ResponseEntity<ApiResponse<EventListResponse>> getEventsByDateRange(
            @PathVariable Long calendarId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        EventListResponse response = eventService.getEventsByCalendarAndDateRange(memberId, calendarId, start, end);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventDetail(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        EventResponse response = eventService.getEventDetail(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/api/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        EventResponse response = eventService.updateEvent(memberId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/api/events/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        eventService.deleteEvent(memberId, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
