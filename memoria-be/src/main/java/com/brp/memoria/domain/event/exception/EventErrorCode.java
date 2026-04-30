package com.brp.memoria.domain.event.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventErrorCode {

    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다."),
    NOT_EVENT_CREATOR(HttpStatus.FORBIDDEN, "이벤트 생성자 또는 관리자만 수행할 수 있습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_AUTHOR(HttpStatus.FORBIDDEN, "댓글 작성자 또는 관리자만 삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
