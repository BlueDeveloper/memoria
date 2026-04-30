package com.brp.memoria.domain.diary.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode {

    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "다이어리를 찾을 수 없습니다."),
    NOT_DIARY_MEMBER(HttpStatus.FORBIDDEN, "해당 다이어리의 멤버가 아닙니다."),
    NOT_DIARY_OWNER(HttpStatus.FORBIDDEN, "다이어리 소유자만 수행할 수 있습니다."),
    NOT_DIARY_ADMIN(HttpStatus.FORBIDDEN, "관리자 이상의 권한이 필요합니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대를 찾을 수 없습니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 초대입니다."),
    ALREADY_MEMBER(HttpStatus.CONFLICT, "이미 해당 다이어리의 멤버입니다."),
    DIARY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "무료 다이어리 생성 한도(2개)를 초과했습니다."),
    INSUFFICIENT_TICKETS(HttpStatus.BAD_REQUEST, "티켓이 부족합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
