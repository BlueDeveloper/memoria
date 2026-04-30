package com.brp.memoria.global.exception;

import com.brp.memoria.domain.auth.exception.AuthException;
import com.brp.memoria.domain.diary.exception.DiaryException;
import com.brp.memoria.domain.event.exception.EventException;
import com.brp.memoria.domain.ticket.exception.TicketException;
import com.brp.memoria.domain.shop.exception.ShopException;
import com.brp.memoria.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {
        log.error("AuthException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getAuthErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getAuthErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(DiaryException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDiaryException(DiaryException e) {
        log.error("DiaryException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getDiaryErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getDiaryErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(EventException.class)
    protected ResponseEntity<ApiResponse<Void>> handleEventException(EventException e) {
        log.error("EventException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getEventErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getEventErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(TicketException.class)
    protected ResponseEntity<ApiResponse<Void>> handleTicketException(TicketException e) {
        log.error("TicketException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getTicketErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getTicketErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(ShopException.class)
    protected ResponseEntity<ApiResponse<Void>> handleShopException(ShopException e) {
        log.error("ShopException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getShopErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getShopErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.name(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("Validation failed: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.name(), message));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_SERVER_ERROR.name(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
