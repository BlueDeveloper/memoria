package com.brp.memoria.domain.event.service;

import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.calendar.entity.CalendarMember;
import com.brp.memoria.domain.calendar.exception.CalendarErrorCode;
import com.brp.memoria.domain.calendar.exception.CalendarException;
import com.brp.memoria.domain.calendar.repository.CalendarMemberRepository;
import com.brp.memoria.domain.calendar.repository.CalendarRepository;
import com.brp.memoria.domain.event.dto.EventCreateRequest;
import com.brp.memoria.domain.event.dto.EventListResponse;
import com.brp.memoria.domain.event.dto.EventResponse;
import com.brp.memoria.domain.event.dto.EventUpdateRequest;
import com.brp.memoria.domain.event.entity.Event;
import com.brp.memoria.domain.event.exception.EventErrorCode;
import com.brp.memoria.domain.event.exception.EventException;
import com.brp.memoria.domain.event.repository.EventRepository;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public EventResponse createEvent(Long memberId, EventCreateRequest request) {
        Calendar calendar = findCalendarById(request.getCalendarId());
        Member member = findMemberById(memberId);
        findCalendarMember(calendar, member);

        Event.RepeatType repeatType = null;
        if (request.getRepeatType() != null) {
            repeatType = Event.RepeatType.valueOf(request.getRepeatType());
        }

        Event event = Event.builder()
                .calendar(calendar)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startDt(request.getStartDt())
                .endDt(request.getEndDt())
                .allDayYn(Boolean.TRUE.equals(request.getAllDay()) ? "Y" : "N")
                .color(request.getColor())
                .repeatType(repeatType)
                .remindMinutes(request.getRemindMinutes())
                .creator(member)
                .build();

        eventRepository.save(event);

        return EventResponse.from(event);
    }

    public EventListResponse getEventsByCalendarAndDateRange(Long memberId, Long calendarId,
                                                              LocalDateTime start, LocalDateTime end) {
        Calendar calendar = findCalendarById(calendarId);
        Member member = findMemberById(memberId);
        findCalendarMember(calendar, member);

        List<Event> events = eventRepository.findByCalendarAndStartDtBetween(calendar, start, end)
                .stream()
                .filter(e -> "N".equals(e.getDelYn()))
                .collect(Collectors.toList());

        List<EventResponse> responses = events.stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());

        return EventListResponse.of(responses);
    }

    public EventResponse getEventDetail(Long memberId, Long eventId) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        findCalendarMember(event.getCalendar(), member);

        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse updateEvent(Long memberId, Long eventId, EventUpdateRequest request) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(event.getCalendar(), member);

        validateCreatorOrAdmin(event, member, calendarMember);

        if (request.getTitle() != null) {
            event.updateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.updateDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.updateLocation(request.getLocation());
        }
        if (request.getStartDt() != null && request.getEndDt() != null) {
            event.updateSchedule(request.getStartDt(), request.getEndDt());
        }
        if (request.getAllDay() != null) {
            event.updateAllDayYn(request.getAllDay() ? "Y" : "N");
        }
        if (request.getColor() != null) {
            event.updateColor(request.getColor());
        }
        if (request.getRepeatType() != null) {
            event.updateRepeatType(Event.RepeatType.valueOf(request.getRepeatType()));
        }
        if (request.getRemindMinutes() != null) {
            event.updateRemindMinutes(request.getRemindMinutes());
        }

        return EventResponse.from(event);
    }

    @Transactional
    public void deleteEvent(Long memberId, Long eventId) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        CalendarMember calendarMember = findCalendarMember(event.getCalendar(), member);

        validateCreatorOrAdmin(event, member, calendarMember);

        event.delete();
    }

    // === 내부 헬퍼 메서드 ===

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .filter(e -> "N".equals(e.getDelYn()))
                .orElseThrow(() -> new EventException(EventErrorCode.EVENT_NOT_FOUND));
    }

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

    private CalendarMember findCalendarMember(Calendar calendar, Member member) {
        return calendarMemberRepository.findByCalendarAndMember(calendar, member)
                .filter(cm -> "N".equals(cm.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.NOT_CALENDAR_MEMBER));
    }

    private void validateCreatorOrAdmin(Event event, Member member, CalendarMember calendarMember) {
        boolean isCreator = event.getCreator().getMemberId().equals(member.getMemberId());
        boolean isAdminOrOwner = calendarMember.getRole() == CalendarMember.CalendarRole.OWNER
                || calendarMember.getRole() == CalendarMember.CalendarRole.ADMIN;

        if (!isCreator && !isAdminOrOwner) {
            throw new EventException(EventErrorCode.NOT_EVENT_CREATOR);
        }
    }
}
