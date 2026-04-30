package com.brp.memoria.domain.diary.exception;

import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DiaryException extends BusinessException {

    private final DiaryErrorCode diaryErrorCode;

    public DiaryException(DiaryErrorCode diaryErrorCode) {
        super(ErrorCode.NOT_FOUND, diaryErrorCode.getMessage());
        this.diaryErrorCode = diaryErrorCode;
    }
}
