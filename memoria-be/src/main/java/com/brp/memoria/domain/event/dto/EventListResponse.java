package com.brp.memoria.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventListResponse {

    private List<EventResponse> events;

    public static EventListResponse of(List<EventResponse> events) {
        return new EventListResponse(events);
    }
}
