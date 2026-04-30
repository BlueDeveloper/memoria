package com.brp.memoria.domain.calendar.service;

import com.brp.memoria.domain.calendar.dto.InvitationResponse;
import com.brp.memoria.domain.calendar.dto.InviteAcceptRequest;
import com.brp.memoria.domain.calendar.dto.InviteRequest;
import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.calendar.entity.CalendarMember;
import com.brp.memoria.domain.calendar.entity.Invitation;
import com.brp.memoria.domain.calendar.exception.CalendarErrorCode;
import com.brp.memoria.domain.calendar.exception.CalendarException;
import com.brp.memoria.domain.calendar.repository.CalendarMemberRepository;
import com.brp.memoria.domain.calendar.repository.CalendarRepository;
import com.brp.memoria.domain.calendar.repository.InvitationRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final CalendarRepository calendarRepository;
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InvitationResponse invite(Long memberId, Long calendarId, InviteRequest request) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .filter(c -> "N".equals(c.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.CALENDAR_NOT_FOUND));

        Member inviter = findMemberById(memberId);

        // 초대자가 캘린더 멤버인지 확인
        calendarMemberRepository.findByCalendarAndMember(calendar, inviter)
                .filter(cm -> "N".equals(cm.getDelYn()))
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.NOT_CALENDAR_MEMBER));

        Invitation.InviteType inviteType = Invitation.InviteType.valueOf(
                request.getInviteType() != null ? request.getInviteType() : "EMAIL"
        );

        Invitation invitation = Invitation.builder()
                .calendar(calendar)
                .inviter(inviter)
                .inviteeEmail(request.getInviteeEmail())
                .inviteType(inviteType)
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        invitationRepository.save(invitation);

        // TODO: 이메일 초대 시 메일 발송 구현

        return InvitationResponse.from(invitation);
    }

    @Transactional
    public void acceptInvite(Long memberId, InviteAcceptRequest request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.INVITATION_NOT_FOUND));

        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new CalendarException(CalendarErrorCode.INVITATION_NOT_FOUND);
        }

        if (invitation.isExpired()) {
            invitation.expire();
            throw new CalendarException(CalendarErrorCode.INVITATION_EXPIRED);
        }

        Member member = findMemberById(memberId);
        Calendar calendar = invitation.getCalendar();

        // 이미 멤버인지 확인
        calendarMemberRepository.findByCalendarAndMember(calendar, member)
                .filter(cm -> "N".equals(cm.getDelYn()))
                .ifPresent(cm -> {
                    throw new CalendarException(CalendarErrorCode.ALREADY_MEMBER);
                });

        CalendarMember calendarMember = CalendarMember.builder()
                .calendar(calendar)
                .member(member)
                .role(CalendarMember.CalendarRole.MEMBER)
                .color(calendar.getColor())
                .build();

        calendarMemberRepository.save(calendarMember);

        invitation.accept();
    }

    public List<InvitationResponse> getMyInvitations(Long memberId) {
        Member member = findMemberById(memberId);

        List<Invitation> invitations = invitationRepository.findByInviteeEmailAndStatus(
                member.getEmail(), Invitation.InvitationStatus.PENDING);

        return invitations.stream()
                .filter(inv -> !inv.isExpired())
                .map(InvitationResponse::from)
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }
}
