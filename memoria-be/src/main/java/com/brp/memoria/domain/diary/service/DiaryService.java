package com.brp.memoria.domain.diary.service;

import com.brp.memoria.domain.diary.dto.*;
import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.diary.exception.DiaryErrorCode;
import com.brp.memoria.domain.diary.exception.DiaryException;
import com.brp.memoria.domain.diary.repository.DiaryMemberRepository;
import com.brp.memoria.domain.diary.repository.DiaryRepository;
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
public class DiaryService {

    private static final int FREE_DIARY_LIMIT = 2;

    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final MemberRepository memberRepository;
    // TODO: TicketService 연동 후 주입
    // private final TicketService ticketService;

    @Transactional
    public DiaryResponse createDiary(Long memberId, DiaryCreateRequest request) {
        Member member = findMemberById(memberId);

        // 다이어리 생성 제한 로직
        long currentDiaryCount = diaryMemberRepository.countByMemberAndDelYn(member, "N");
        if (currentDiaryCount >= FREE_DIARY_LIMIT) {
            // TODO: TicketService 구현 후 티켓 차감 로직 연동
            // ticketService.use(memberId, DIARY_TICKET_COST, "DIARY", null, "추가 다이어리 생성");
            throw new DiaryException(DiaryErrorCode.DIARY_LIMIT_EXCEEDED);
        }

        Diary diary = Diary.builder()
                .name(request.getName())
                .description(request.getDescription())
                .color(request.getColor())
                .inviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .owner(member)
                .diaryType(request.getDiaryType())
                .build();

        diaryRepository.save(diary);

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .role(DiaryMember.DiaryRole.OWNER)
                .color(diary.getColor())
                .build();

        diaryMemberRepository.save(diaryMember);

        return DiaryResponse.of(diary, 1, DiaryMember.DiaryRole.OWNER);
    }

    public DiaryListResponse getMyDiaries(Long memberId) {
        Member member = findMemberById(memberId);

        List<DiaryMember> diaryMembers = diaryMemberRepository.findByMemberAndDelYn(member, "N");

        List<DiaryResponse> responses = diaryMembers.stream()
                .filter(dm -> "N".equals(dm.getDiary().getDelYn()))
                .map(dm -> {
                    Diary diary = dm.getDiary();
                    int memberCount = diaryMemberRepository.findByDiaryAndDelYn(diary, "N").size();
                    return DiaryResponse.of(diary, memberCount, dm.getRole());
                })
                .collect(Collectors.toList());

        return DiaryListResponse.of(responses);
    }

    public DiaryResponse getDiaryDetail(Long memberId, Long diaryId) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        DiaryMember diaryMember = findDiaryMember(diary, member);

        int memberCount = diaryMemberRepository.findByDiaryAndDelYn(diary, "N").size();

        return DiaryResponse.of(diary, memberCount, diaryMember.getRole());
    }

    @Transactional
    public DiaryResponse updateDiary(Long memberId, Long diaryId, DiaryUpdateRequest request) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        DiaryMember diaryMember = findDiaryMember(diary, member);

        validateAdminOrOwner(diaryMember);

        if (request.getName() != null) {
            diary.updateName(request.getName());
        }
        if (request.getDescription() != null) {
            diary.updateDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            diary.updateColor(request.getColor());
        }

        int memberCount = diaryMemberRepository.findByDiaryAndDelYn(diary, "N").size();

        return DiaryResponse.of(diary, memberCount, diaryMember.getRole());
    }

    @Transactional
    public void deleteDiary(Long memberId, Long diaryId) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        DiaryMember diaryMember = findDiaryMember(diary, member);

        if (diaryMember.getRole() != DiaryMember.DiaryRole.OWNER) {
            throw new DiaryException(DiaryErrorCode.NOT_DIARY_OWNER);
        }

        diary.delete();
    }

    public List<DiaryMemberResponse> getDiaryMembers(Long memberId, Long diaryId) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        findDiaryMember(diary, member);

        List<DiaryMember> members = diaryMemberRepository.findByDiaryAndDelYn(diary, "N");

        return members.stream()
                .map(DiaryMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMemberColor(Long memberId, Long diaryId, MemberColorUpdateRequest request) {
        Diary diary = findDiaryById(diaryId);
        Member member = findMemberById(memberId);
        DiaryMember diaryMember = findDiaryMember(diary, member);

        diaryMember.updateColor(request.getColor());
    }

    // === 내부 헬퍼 메서드 ===

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

    DiaryMember findDiaryMember(Diary diary, Member member) {
        return diaryMemberRepository.findByDiaryAndMember(diary, member)
                .filter(dm -> "N".equals(dm.getDelYn()))
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NOT_DIARY_MEMBER));
    }

    private void validateAdminOrOwner(DiaryMember diaryMember) {
        if (diaryMember.getRole() != DiaryMember.DiaryRole.OWNER
                && diaryMember.getRole() != DiaryMember.DiaryRole.ADMIN) {
            throw new DiaryException(DiaryErrorCode.NOT_DIARY_ADMIN);
        }
    }
}
