package com.lyx.secondhandsystem.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// 全局异常处理类
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException e) {
        // 提取验证错误消息
        String msg = e.getBindingResult().getAllErrors().stream()
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getField() + ":" + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                }).collect(Collectors.joining(", "));
        // 返回包含验证错误消息的错误响应
        return ApiResponse.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), msg);
    }

    // 处理 IllegalArgumentException 异常
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        // 返回包含 IllegalArgumentException 消息的错误响应
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
