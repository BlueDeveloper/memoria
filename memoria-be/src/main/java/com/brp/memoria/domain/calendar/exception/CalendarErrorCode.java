package com.brp.memoria.domain.calendar.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode {

    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "캘린더를 찾을 수 없습니다."),
    NOT_CALENDAR_MEMBER(HttpStatus.FORBIDDEN, "해당 캘린더의 멤버가 아닙니다."),
    NOT_CALENDAR_OWNER(HttpStatus.FORBIDDEN, "캘린더 소유자만 수행할 수 있습니다."),
    NOT_CALENDAR_ADMIN(HttpStatus.FORBIDDEN, "관리자 이상의 권한이 필요합니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대를 찾을 수 없습니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 초대입니다."),
    ALREADY_MEMBER(HttpStatus.CONFLICT, "이미 해당 캘린더의 멤버입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
