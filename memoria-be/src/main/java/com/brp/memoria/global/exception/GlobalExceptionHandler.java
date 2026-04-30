package com.brp.memoria.global.exception;

import com.brp.memoria.domain.auth.exception.AuthException;
import com.brp.memoria.domain.calendar.exception.CalendarException;
import com.brp.memoria.domain.event.exception.EventException;
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

    @ExceptionHandler(CalendarException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCalendarException(CalendarException e) {
        log.error("CalendarException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getCalendarErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getCalendarErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(EventException.class)
    protected ResponseEntity<ApiResponse<Void>> handleEventException(EventException e) {
        log.error("EventException: {}", e.getMessage());
        return ResponseEntity
                .status(e.getEventErrorCode().getHttpStatus())
                .body(ApiResponse.fail(e.getEventErrorCode().name(), e.getMessage()));
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
