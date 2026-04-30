package com.brp.memoria.domain.calendar.service;

import com.brp.memoria.domain.calendar.dto.*;
import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.calendar.entity.CalendarMember;
import com.brp.memoria.domain.calendar.exception.CalendarErrorCode;
import com.brp.memoria.domain.calendar.exception.CalendarException;
import com.brp.memoria.domain.calendar.repository.CalendarMemberRepository;
import com.brp.memoria.domain.calendar.repository.CalendarRepository;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CalendarResponse createCalendar(Long memberId, CalendarCreateRequest request) {
        Member member = findMemberById(memberId);

        Calendar calendar = Calendar.builder()
                .name(request.getName())
                .description(request.getDescription())
                .color(request.getColor())
                .inviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .owner(member)
                .build();

        calendarRepository.save(calendar);

        CalendarMember calendarMember = CalendarMember.builder()
                .calendar(calendar)
                .member(member)
                .role(CalendarMember.CalendarRole.OWNER)
                .color(calendar.getColor())
                .build();

        calendarMemberRepository.save(calendarMember);

        return CalendarResponse.of(calendar, 1, CalendarMember.CalendarRole.OWNER);
    }

    public CalendarListResponse getMyCalendars(Long memberId) {
        Member member = findMemberById(memberId);

        List<CalendarMember> calendarMembers = calendarMemberRepository.findByMemberAndDelYn(member, "N");

        List<CalendarResponse> responses = calendarMembers.stream()
                .filter(cm -> "N".equals(cm.getCalendar().getDelYn()))
                .map(cm -> {
                    Calendar calendar = cm.getCalendar();
                    int memberCount = calendarMemberRepository.findByCalendarAndDelYn(calendar, "N").size();
                    return CalendarResponse.of(calendar, memberCount, cm.getRole());
                })
                .collect(Collectors.toList());

        return CalendarListResponse.of(responses);
    }

    public CalendarResponse getCalendarDetail(Long memberId, Long calendarId) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(calendar, member);

        int memberCount = calendarMemberRepository.findByCalendarAndDelYn(calendar, "N").size();

        return CalendarResponse.of(calendar, memberCount, calendarMember.getRole());
    }

    @Transactional
    public CalendarResponse updateCalendar(Long memberId, Long calendarId, CalendarUpdateRequest request) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(calendar, member);

        validateAdminOrOwner(calendarMember);

        if (request.getName() != null) {
            calendar.updateName(request.getName());
        }
        if (request.getDescription() != null) {
            calendar.updateDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            calendar.updateColor(request.getColor());
        }

        int memberCount = calendarMemberRepository.findByCalendarAndDelYn(calendar, "N").size();

        return CalendarResponse.of(calendar, memberCount, calendarMember.getRole());
    }

    @Transactional
    public void deleteCalendar(Long memberId, Long calendarId) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(calendar, member);

        if (calendarMember.getRole() != CalendarMember.CalendarRole.OWNER) {
            throw new CalendarException(CalendarErrorCode.NOT_CALENDAR_OWNER);
        }

        calendar.delete();
    }

    public List<CalendarMemberResponse> getCalendarMembers(Long memberId, Long calendarId) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        findCalendarMember(calendar, member);

        List<CalendarMember> members = calendarMemberRepository.findByCalendarAndDelYn(calendar, "N");

        return members.stream()
                .map(CalendarMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMemberColor(Long memberId, Long calendarId, MemberColorUpdateRequest request) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(calendar, member);

        calendarMember.updateColor(request.getColor());
    }

    // === 내부 헬퍼 메서드 ===

    private Calendar findCalendarById(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .filter(c -> "N".equals(c.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.CALENDAR_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    CalendarMember findCalendarMember(Calendar calendar, Member member) {
        return calendarMemberRepository.findByCalendarAndMember(calendar, member)
                .filter(cm -> "N".equals(cm.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.NOT_CALENDAR_MEMBER));
    }

    private void validateAdminOrOwner(CalendarMember calendarMember) {
        if (calendarMember.getRole() != CalendarMember.CalendarRole.OWNER
                && calendarMember.getRole() != CalendarMember.CalendarRole.ADMIN) {
            throw new CalendarException(CalendarErrorCode.NOT_CALENDAR_ADMIN);
        }
    }
}
