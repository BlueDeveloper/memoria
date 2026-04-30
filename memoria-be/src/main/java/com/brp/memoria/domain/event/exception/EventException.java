package com.brp.memoria.domain.event.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class EventException extends BusinessException {

    private final EventErrorCode eventErrorCode;

    public EventException(EventErrorCode eventErrorCode) {
        super(ErrorCode.NOT_FOUND, eventErrorCode.getMessage());
        this.eventErrorCode = eventErrorCode;
    }
}
