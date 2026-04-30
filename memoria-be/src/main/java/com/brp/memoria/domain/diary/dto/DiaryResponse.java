package com.brp.memoria.domain.diary.dto;

import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryResponse {

    private Long diaryId;
    private String name;
    private String description;
    private String color;
    private String inviteCode;
    private String ownerNickname;
    private int memberCount;
    private String myRole;
    private String diaryType;
    private Long logoId;
    private Long themeId;
    private LocalDateTime createdAt;

    public static DiaryResponse of(Diary diary, int memberCount, DiaryMember.DiaryRole myRole) {
        return new DiaryResponse(
                diary.getDiaryId(),
                diary.getName(),
                diary.getDescription(),
                diary.getColor(),
                diary.getInviteCode(),
                diary.getOwner().getNickname(),
                memberCount,
                myRole != null ? myRole.name() : null,
                diary.getDiaryType(),
                diary.getLogoId(),
                diary.getThemeId(),
                diary.getCreDt()
        );
    }
}
