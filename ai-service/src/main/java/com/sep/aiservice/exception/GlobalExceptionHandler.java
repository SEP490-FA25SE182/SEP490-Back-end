package com.sep.aiservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Bắt lỗi từ third-party (Meshy, HTTP client, timeout, v.v.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Lỗi không mong muốn tại {} {} – Request: {}",
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Đã có lỗi xảy ra, vui lòng thử lại sau",
                ex.getMessage() // chỉ để dev thấy, production có thể ẩn
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Bắt riêng lỗi từ Meshy (nếu bạn throw RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, WebRequest request) {
        log.warn("RuntimeException: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("ERROR", ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Bắt lỗi validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse(ex.getMessage());

        log.warn("Validation error: {}", msg);
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", msg, null));
    }
}
