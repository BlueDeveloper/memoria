package com.brp.memoria.domain.ticket.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketBalanceResponse {

    private int balance;

    public static TicketBalanceResponse of(int balance) {
        return new TicketBalanceResponse(balance);
    }
}
