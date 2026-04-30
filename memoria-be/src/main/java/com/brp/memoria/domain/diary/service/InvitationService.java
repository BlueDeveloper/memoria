package com.brp.memoria.domain.diary.service;

import com.brp.memoria.domain.diary.dto.InvitationResponse;
import com.brp.memoria.domain.diary.dto.InviteAcceptRequest;
import com.brp.memoria.domain.diary.dto.InviteRequest;
import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.diary.entity.Invitation;
import com.brp.memoria.domain.diary.exception.DiaryErrorCode;
import com.brp.memoria.domain.diary.exception.DiaryException;
import com.brp.memoria.domain.diary.repository.DiaryMemberRepository;
import com.brp.memoria.domain.diary.repository.DiaryRepository;
import com.brp.memoria.domain.diary.repository.InvitationRepository;
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
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InvitationResponse invite(Long memberId, Long diaryId, InviteRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
                .filter(d -> "N".equals(d.getDelYn()))
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND));

        Member inviter = findMemberById(memberId);

        // 초대자가 다이어리 멤버인지 확인
        diaryMemberRepository.findByDiaryAndMember(diary, inviter)
                .filter(dm -> "N".equals(dm.getDelYn()))
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NOT_DIARY_MEMBER));

        Invitation.InviteType inviteType = Invitation.InviteType.valueOf(
                request.getInviteType() != null ? request.getInviteType() : "EMAIL"
        );

        Invitation invitation = Invitation.builder()
                .diary(diary)
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
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.INVITATION_NOT_FOUND));

        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new DiaryException(DiaryErrorCode.INVITATION_NOT_FOUND);
        }

        if (invitation.isExpired()) {
            invitation.expire();
            throw new DiaryException(DiaryErrorCode.INVITATION_EXPIRED);
        }

        Member member = findMemberById(memberId);
        Diary diary = invitation.getDiary();

        // 이미 멤버인지 확인
        diaryMemberRepository.findByDiaryAndMember(diary, member)
                .filter(dm -> "N".equals(dm.getDelYn()))
                .ifPresent(dm -> {
                    throw new DiaryException(DiaryErrorCode.ALREADY_MEMBER);
                });

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .role(DiaryMember.DiaryRole.MEMBER)
                .color(diary.getColor())
                .build();

        diaryMemberRepository.save(diaryMember);

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
