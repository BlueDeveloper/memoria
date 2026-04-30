package com.brp.memoria.domain.auth.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends BusinessException {

    private final AuthErrorCode authErrorCode;

    public AuthException(AuthErrorCode authErrorCode) {
        super(ErrorCode.UNAUTHORIZED, authErrorCode.getMessage());
        this.authErrorCode = authErrorCode;
    }
}
