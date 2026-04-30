package com.brp.memoria.domain.ticket.dto;

import com.brp.memoria.domain.ticket.entity.TicketTransaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketTransactionResponse {

    private Long transactionId;
    private String type;
    private int amount;
    private int balanceAfter;
    private String itemType;
    private Long itemId;
    private String description;
    private LocalDateTime createdAt;

    public static TicketTransactionResponse from(TicketTransaction transaction) {
        return new TicketTransactionResponse(
                transaction.getTransactionId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getItemType(),
                transaction.getItemId(),
                transaction.getDescription(),
                transaction.getCreDt()
        );
    }
}
