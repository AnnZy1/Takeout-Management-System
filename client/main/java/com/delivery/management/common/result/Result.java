package com.delivery.management.common.result;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一返回结果类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     * 200 - 成功
     * 400 - 参数错误
     * 401 - 未登录/Token过期
     * 403 - 无权限
     * 500 - 系统异常
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 私有构造方法
     */
    private Result() {
    }

    /**
     * 私有构造方法
     * 
     * @param code 响应码
     * @param msg 提示信息
     * @param data 业务数据
     */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     * 
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     * 
     * @param data 业务数据
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     * 
     * @param msg 提示信息
     * @return Result
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.SUCCESS, msg, null);
    }

    /**
     * 成功响应（自定义消息和数据）
     * 
     * @param msg 提示信息
     * @param data 业务数据
     * @return Result
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS, msg, data);
    }

    /**
     * 失败响应
     * 
     * @param msg 错误信息
     * @return Result
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.ERROR, msg, null);
    }

    /**
     * 失败响应（自定义响应码）
     * 
     * @param code 响应码
     * @param msg 错误信息
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 参数错误响应
     * 
     * @param msg 错误信息
     * @return Result
     */
    public static <T> Result<T> badRequest(String msg) {
        return new Result<>(ResultCode.BAD_REQUEST, msg, null);
    }

    /**
     * 未登录响应
     * 
     * @param msg 错误信息
     * @return Result
     */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(ResultCode.UNAUTHORIZED, msg, null);
    }

    /**
     * 无权限响应
     * 
     * @param msg 错误信息
     * @return Result
     */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(ResultCode.FORBIDDEN, msg, null);
    }

    /**
     * 响应码常量类
     */
    public static class ResultCode {
        /**
         * 成功
         */
        public static final Integer SUCCESS = 200;

        /**
         * 参数错误
         */
        public static final Integer BAD_REQUEST = 400;

        /**
         * 未登录/Token过期
         */
        public static final Integer UNAUTHORIZED = 401;

        /**
         * 无权限
         */
        public static final Integer FORBIDDEN = 403;

        /**
         * 系统异常
         */
        public static final Integer ERROR = 500;
    }
}

