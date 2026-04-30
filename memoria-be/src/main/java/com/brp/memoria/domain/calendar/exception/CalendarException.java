package com.brp.memoria.domain.calendar.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CalendarException extends BusinessException {

    private final CalendarErrorCode calendarErrorCode;

    public CalendarException(CalendarErrorCode calendarErrorCode) {
        super(ErrorCode.NOT_FOUND, calendarErrorCode.getMessage());
        this.calendarErrorCode = calendarErrorCode;
    }
}
