package com.brp.memoria.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCreateRequest {

    @NotNull(message = "캘린더 ID는 필수 입력값입니다.")
    private Long calendarId;

    @NotBlank(message = "일정 제목은 필수 입력값입니다.")
    @Size(max = 300, message = "일정 제목은 300자 이하로 입력해주세요.")
    private String title;

    @Size(max = 2000, message = "설명은 2000자 이하로 입력해주세요.")
    private String description;

    @Size(max = 500, message = "장소는 500자 이하로 입력해주세요.")
    private String location;

    @NotNull(message = "시작 일시는 필수 입력값입니다.")
    private LocalDateTime startDt;

    @NotNull(message = "종료 일시는 필수 입력값입니다.")
    private LocalDateTime endDt;

    private Boolean allDay;

    private String color;

    private String repeatType;

    private Integer remindMinutes;
}
