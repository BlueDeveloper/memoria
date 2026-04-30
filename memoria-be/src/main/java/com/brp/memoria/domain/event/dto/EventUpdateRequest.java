package com.brp.memoria.domain.event.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventUpdateRequest {

    @Size(max = 300, message = "일정 제목은 300자 이하로 입력해주세요.")
    private String title;

    @Size(max = 2000, message = "설명은 2000자 이하로 입력해주세요.")
    private String description;

    @Size(max = 500, message = "장소는 500자 이하로 입력해주세요.")
    private String location;

    private LocalDateTime startDt;

    private LocalDateTime endDt;

    private Boolean allDay;

    private String color;

    private String repeatType;

    private Integer remindMinutes;
}
