package com.brp.memoria.domain.shop.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ShopException extends BusinessException {

    private final ShopErrorCode shopErrorCode;

    public ShopException(ShopErrorCode shopErrorCode) {
        super(ErrorCode.NOT_FOUND, shopErrorCode.getMessage());
        this.shopErrorCode = shopErrorCode;
    }
}
