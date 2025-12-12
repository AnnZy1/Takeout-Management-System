package com.delivery.management.common.exception;

import com.delivery.management.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param ex 业务异常
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        log.error("业务异常：{}", ex.getMsg(), ex);
        return Result.error(ex.getCode() != null ? ex.getCode() : Result.ResultCode.ERROR, ex.getMsg());
    }

    /**
     * 处理参数校验异常（@Valid 注解）
     * 
     * @param ex 参数校验异常
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("参数校验异常：{}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.badRequest("参数校验失败：" + errorMessage);
    }

    /**
     * 处理参数绑定异常（@Validated 注解）
     * 
     * @param ex 参数绑定异常
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException ex) {
        log.error("参数绑定异常：{}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.badRequest("参数绑定失败：" + errorMessage);
    }

    /**
     * 处理数据库唯一约束异常
     * 
     * @param ex 唯一约束异常
     * @return Result
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        log.error("数据库唯一约束异常：{}", ex.getMessage(), ex);
        String message = ex.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            String[] split = message.split("'");
            if (split.length > 1) {
                return Result.error(Result.ResultCode.BAD_REQUEST, split[1] + "已存在");
            }
        }
        return Result.error("数据已存在，请勿重复添加");
    }

    /**
     * 处理非法参数异常
     * 
     * @param ex 非法参数异常
     * @return Result
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("非法参数异常：{}", ex.getMessage(), ex);
        return Result.badRequest(ex.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param ex 运行时异常
     * @return Result
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常：{}", ex.getMessage(), ex);
        return Result.error("系统异常，请联系管理员");
    }

    /**
     * 处理所有其他异常
     * 
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        log.error("系统异常：{}", ex.getMessage(), ex);
        return Result.error("系统异常，请联系管理员");
    }
}

