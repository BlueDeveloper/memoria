package com.brp.memoria.domain.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseRequest {

    @NotNull(message = "아이템 ID는 필수 입력값입니다.")
    private Long itemId;
}
