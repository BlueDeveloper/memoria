package com.brp.memoria.domain.calendar.dto;

import com.brp.memoria.domain.calendar.entity.CalendarMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarMemberResponse {

    private Long memberId;
    private String nickname;
    private String profileImage;
    private String color;
    private String role;

    public static CalendarMemberResponse from(CalendarMember calendarMember) {
        return new CalendarMemberResponse(
                calendarMember.getMember().getMemberId(),
                calendarMember.getMember().getNickname(),
                calendarMember.getMember().getProfileImage(),
                calendarMember.getColor(),
                calendarMember.getRole().name()
        );
    }
}
