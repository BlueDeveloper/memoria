package com.brp.memoria.domain.ticket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketChargeRequest {

    @NotNull(message = "충전 수량은 필수 입력값입니다.")
    @Min(value = 1, message = "충전 수량은 1 이상이어야 합니다.")
    private Integer amount;

    private String description;
}
