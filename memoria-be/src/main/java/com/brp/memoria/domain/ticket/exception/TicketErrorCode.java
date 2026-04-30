package com.brp.memoria.domain.ticket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketErrorCode {

    INSUFFICIENT_TICKETS(HttpStatus.BAD_REQUEST, "티켓이 부족합니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 수량입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
