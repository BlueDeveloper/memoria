package com.brp.memoria.domain.shop.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShopErrorCode {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유한 아이템입니다."),
    ITEM_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "현재 판매 중이지 않은 아이템입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
