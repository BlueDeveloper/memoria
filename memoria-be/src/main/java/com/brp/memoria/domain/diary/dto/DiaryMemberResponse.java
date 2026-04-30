package com.brp.memoria.domain.diary.dto;

import com.brp.memoria.domain.diary.entity.DiaryMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryMemberResponse {

    private Long memberId;
    private String nickname;
    private String profileImage;
    private String color;
    private String role;

    public static DiaryMemberResponse from(DiaryMember diaryMember) {
        return new DiaryMemberResponse(
                diaryMember.getMember().getMemberId(),
                diaryMember.getMember().getNickname(),
                diaryMember.getMember().getProfileImage(),
                diaryMember.getColor(),
                diaryMember.getRole().name()
        );
    }
}
