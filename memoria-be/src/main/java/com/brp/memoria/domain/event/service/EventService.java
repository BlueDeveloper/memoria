package com.brp.memoria.domain.event.service;

import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.diary.exception.DiaryErrorCode;
import com.brp.memoria.domain.diary.exception.DiaryException;
import com.brp.memoria.domain.diary.repository.DiaryMemberRepository;
import com.brp.memoria.domain.diary.repository.DiaryRepository;
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
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public EventResponse createEvent(Long memberId, EventCreateRequest request) {
        Diary diary = findDiaryById(request.getDiaryId());
        Member member = findMemberById(memberId);
        findDiaryMember(diary, member);

        Event.RepeatType repeatType = null;
        if (request.getRepeatType() != null) {
            repeatType = Event.RepeatType.valueOf(request.getRepeatType());
        }

        Event event = Event.builder()
                .diary(diary)
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

    public EventListResponse getEventsByDiaryAndDateRange(Long memberId, Long diaryId,
                                                           LocalDateTime start, LocalDateTime end) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        findDiaryMember(diary, member);

        List<Event> events = eventRepository.findByDiaryAndStartDtBetween(diary, start, end)
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
        findDiaryMember(event.getDiary(), member);

        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse updateEvent(Long memberId, Long eventId, EventUpdateRequest request) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        DiaryMember diaryMember = findDiaryMember(event.getDiary(), member);

        validateCreatorOrAdmin(event, member, diaryMember);

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
        DiaryMember diaryMember = findDiaryMember(event.getDiary(), member);

        validateCreatorOrAdmin(event, member, diaryMember);

        event.delete();
    }

    // === 내부 헬퍼 메서드 ===

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .filter(e -> "N".equals(e.getDelYn()))
                .orElseThrow(() -> new EventException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private Diary findDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .filter(d -> "N".equals(d.getDelYn()))
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    private DiaryMember findDiaryMember(Diary diary, Member member) {
        return diaryMemberRepository.findByDiaryAndMember(diary, member)
                .filter(dm -> "N".equals(dm.getDelYn()))
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NOT_DIARY_MEMBER));
    }

    private void validateCreatorOrAdmin(Event event, Member member, DiaryMember diaryMember) {
        boolean isCreator = event.getCreator().getMemberId().equals(member.getMemberId());
        boolean isAdminOrOwner = diaryMember.getRole() == DiaryMember.DiaryRole.OWNER
                || diaryMember.getRole() == DiaryMember.DiaryRole.ADMIN;

        if (!isCreator && !isAdminOrOwner) {
            throw new EventException(EventErrorCode.NOT_EVENT_CREATOR);
        }
    }
}
