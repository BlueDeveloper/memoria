package com.brp.memoria.domain.event.service;

import com.brp.memoria.domain.calendar.entity.CalendarMember;
import com.brp.memoria.domain.calendar.exception.CalendarErrorCode;
import com.brp.memoria.domain.calendar.exception.CalendarException;
import com.brp.memoria.domain.calendar.repository.CalendarMemberRepository;
import com.brp.memoria.domain.event.dto.CommentCreateRequest;
import com.brp.memoria.domain.event.dto.CommentResponse;
import com.brp.memoria.domain.event.entity.Event;
import com.brp.memoria.domain.event.entity.EventComment;
import com.brp.memoria.domain.event.exception.EventErrorCode;
import com.brp.memoria.domain.event.exception.EventException;
import com.brp.memoria.domain.event.repository.EventCommentRepository;
import com.brp.memoria.domain.event.repository.EventRepository;
import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final EventCommentRepository eventCommentRepository;
    private final EventRepository eventRepository;
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponse addComment(Long memberId, Long eventId, CommentCreateRequest request) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        findCalendarMember(event, member);

        EventComment comment = EventComment.builder()
                .event(event)
                .member(member)
                .content(request.getContent())
                .build();

        eventCommentRepository.save(comment);

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getComments(Long memberId, Long eventId) {
        Event event = findEventById(eventId);
        Member member = findMemberById(memberId);
        findCalendarMember(event, member);

        List<EventComment> comments = eventCommentRepository.findByEventAndDelYn(event, "N");

        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        EventComment comment = eventCommentRepository.findById(commentId)
                .filter(c -> "N".equals(c.getDelYn()))
                .orElseThrow(() -> new EventException(EventErrorCode.COMMENT_NOT_FOUND));

        Member member = findMemberById(memberId);

        boolean isAuthor = comment.getMember().getMemberId().equals(member.getMemberId());

        if (!isAuthor) {
            // 관리자/소유자인지 확인
            CalendarMember calendarMember = findCalendarMember(comment.getEvent(), member);
            boolean isAdminOrOwner = calendarMember.getRole() == CalendarMember.CalendarRole.OWNER
                    || calendarMember.getRole() == CalendarMember.CalendarRole.ADMIN;

            if (!isAdminOrOwner) {
                throw new EventException(EventErrorCode.NOT_COMMENT_AUTHOR);
            }
        }

        comment.delete();
    }

    // === 내부 헬퍼 메서드 ===

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .filter(e -> "N".equals(e.getDelYn()))
                .orElseThrow(() -> new EventException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    private CalendarMember findCalendarMember(Event event, Member member) {
        return calendarMemberRepository.findByCalendarAndMember(event.getCalendar(), member)
                .filter(cm -> "N".equals(cm.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.NOT_CALENDAR_MEMBER));
    }
}
