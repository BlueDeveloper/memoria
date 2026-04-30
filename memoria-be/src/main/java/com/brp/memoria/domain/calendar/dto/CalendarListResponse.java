package com.brp.memoria.domain.calendar.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarListResponse {

    private List<CalendarResponse> calendars;

    public static CalendarListResponse of(List<CalendarResponse> calendars) {
        return new CalendarListResponse(calendars);
    }
}
