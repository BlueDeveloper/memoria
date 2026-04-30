package com.brp.memoria.domain.calendar.dto;

import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.calendar.entity.CalendarMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarResponse {

    private Long calendarId;
    private String name;
    private String description;
    private String color;
    private String inviteCode;
    private String ownerNickname;
    private int memberCount;
    private String myRole;
    private LocalDateTime createdAt;

    public static CalendarResponse of(Calendar calendar, int memberCount, CalendarMember.CalendarRole myRole) {
        return new CalendarResponse(
                calendar.getCalendarId(),
                calendar.getName(),
                calendar.getDescription(),
                calendar.getColor(),
                calendar.getInviteCode(),
                calendar.getOwner().getNickname(),
                memberCount,
                myRole != null ? myRole.name() : null,
                calendar.getCreDt()
        );
    }
}
