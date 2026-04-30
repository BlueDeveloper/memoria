package com.brp.memoria.domain.event.dto;

import com.brp.memoria.domain.event.entity.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventResponse {

    private Long eventId;
    private Long calendarId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private boolean allDay;
    private String color;
    private String repeatType;
    private Integer remindMinutes;
    private String creatorNickname;
    private LocalDateTime createdAt;

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getCalendar().getCalendarId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDt(),
                event.getEndDt(),
                "Y".equals(event.getAllDayYn()),
                event.getColor(),
                event.getRepeatType() != null ? event.getRepeatType().name() : null,
                event.getRemindMinutes(),
                event.getCreator().getNickname(),
                event.getCreDt()
        );
    }
}
