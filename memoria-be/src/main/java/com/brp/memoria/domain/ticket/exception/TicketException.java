package com.brp.memoria.domain.ticket.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class TicketException extends BusinessException {

    private final TicketErrorCode ticketErrorCode;

    public TicketException(TicketErrorCode ticketErrorCode) {
        super(ErrorCode.INVALID_INPUT, ticketErrorCode.getMessage());
        this.ticketErrorCode = ticketErrorCode;
    }
}
